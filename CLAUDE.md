# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CloudSim Plus is a discrete event simulation framework for cloud computing infrastructures (datacenters, hosts, VMs, cloudlets/tasks). It's a Java library published to Maven Central under `org.cloudsimplus:cloudsimplus`. This repository holds only the framework API; examples, testbeds and benchmarks live in separate repositories (`cloudsimplus-examples`, `cloudsimplus-testbeds`, `cloudsimplus-benchmarks`), so there is no `main` program to run here.

## Build Commands

Use `./mvnw` (or `mvnw.cmd` on Windows) if Maven isn't installed. Requires JDK 25.

```bash
# Build and run unit tests (default profile excludes **/integrationtests/*)
mvn clean install

# Run unit + integration tests (same command used by CI in .github/workflows/build.yml)
mvn clean verify -Pintegration-tests

# Run a single test class / method
mvn test -Dtest=VmSimpleTest
mvn test -Dtest=VmSimpleTest#testMethod

# Run a single integration test (the profile is required, otherwise it's excluded)
mvn test -Pintegration-tests -Dtest=VmCreationFailureIntegrationTest

# Coverage report (JaCoCo agent is always attached) -> target/site/jacoco
mvn test jacoco:report

# Add missing GPLv3 license headers to source files
mvn license:format
```

There is no Maven lint step: `checkstyle.xml` is only used by IDEs, it is not wired into the build. The `license-maven-plugin` `check` goal runs in `process-sources` on every build. Javadoc and source jars are built in the default profile (`failOnError=false`). The `sonatype` profile (`mvn clean deploy -Psonatype`) is only for releasing to Maven Central.

## Architecture

### Simulation engine (`core`, `core.events`)

- `CloudSimPlus` (extends the package-private abstract `CloudSim`, which implements the sealed `Simulation`) owns the clock, the entity list, a `FutureQueue` and a `DeferredQueue`.
- `start()` starts all entities and then loops `processEvents()`. Each iteration first runs every `RUNNABLE` entity, then takes the earliest event from the future queue, moves the clock to its time and processes **all events with that same time** together. Events in the past throw an exception.
- A `SEND` event goes straight to its destination entity if the entity is `WAITING` (and matches its wait predicate). Otherwise it goes to the deferred queue, which `CloudSimEntity` reads in its `run()` and passes to `processEvent(SimEvent)`.
- Event types are identified by `CloudSimTag` (e.g. `VM_CREATE_ACK`, `CLOUDLET_SUBMIT`, `CLOUDLET_RETURN`, `VM_UPDATE_CLOUDLET_PROCESSING`). Event data is passed as an untyped `Object`.
- The simulation ends when there are no future events (or the `terminateAt()` time is reached). `SIMULATION_END` is then sent so entities can process their last events. `startSync()` + `runFor(interval)` allow running the simulation step by step, and `pause()`/`resume()` are also supported. `minTimeBetweenEvents` (default 0.1s) sets the smallest time step the simulation tracks.
- `CloudInformationService` (CIS) is an internal entity created by the simulation. Datacenters register with it and brokers ask it for the datacenter list.

### Entities vs. plain objects

Only `SimEntity` subclasses receive events: `Datacenter`, `DatacenterBroker`, `CloudInformationService`, network switches and `HostFaultInjection`. **Host, Vm and Cloudlet are not entities.** Their state is updated from outside:

1. The Datacenter schedules a `VM_UPDATE_CLOUDLET_PROCESSING` event for itself.
2. It calls `Host.updateProcessing(time)` for each host. The host updates its VMs, and each VM updates its `CloudletScheduler` using the MIPS share the `VmScheduler` gave it.
3. Each layer returns the delay until the next expected cloudlet completion. The Datacenter takes the minimum (at least `minTimeBetweenEvents`, adjusted by `Datacenter.schedulingInterval` when set) and schedules the next update. It also checks if VM migrations are needed.

So changes to cloudlet or VM execution logic usually affect this update chain and the delay value it returns.

### Typical interaction flow

`DatacenterBroker` (the customer agent) → asks CIS for datacenters (`DC_LIST_REQUEST`) → sends VMs to a datacenter. The Datacenter places them on hosts using its `VmAllocationPolicy` and answers with `VM_CREATE_ACK`. Failed VMs are retried in the next datacenter or later (`VM_CREATE_RETRY`). The broker then submits cloudlets (`CLOUDLET_SUBMIT`) to the VMs it picks, and finished cloudlets come back with `CLOUDLET_RETURN`. Broker choices can be customized without subclassing via `setVmMapper`, `setDatacenterMapper`, `setVmComparator` and `setCloudletComparator`.

### Pluggable policies (Strategy pattern)

| Concern | Interface | Used by |
|---------|-----------|---------|
| VM → Host placement / migration | `VmAllocationPolicy` (+ `allocationpolicies.migration`) | Datacenter |
| Which VM to migrate | `VmSelectionPolicy` | migration allocation policies |
| Host CPU cores → VMs | `VmScheduler` (time/space shared) | Host |
| VM CPU cores → Cloudlets | `CloudletScheduler` (time/space shared, completely fair) | Vm |
| RAM/BW provisioning | `ResourceProvisioner` | Host |
| Resource usage over time | `UtilizationModel` | Cloudlet |
| Power consumption | `PowerModel` | Host / Datacenter |
| Scaling | `HorizontalVmScaling`, `VerticalVmScaling` (`autoscaling`) | Broker / Vm |

### Key design conventions

- **Sealed interfaces**: `Simulation`, `Machine`, `Vm`, `Host`, `Cloudlet`, `DatacenterBroker`, `SimEvent`, `UtilizationModel`, `PowerModelHost` and others are sealed, usually permitting only an `*Abstract` base and a `*Null` class. Adding a new direct implementation means updating the `permits` clause; normally you extend the `*Abstract`/`*Simple` class instead.
- **Null Object pattern**: most interfaces have a `NULL` constant (`Vm.NULL`, `Host.NULL`, `SimEntity.NULL`...) backed by a `*Null` class. Return and compare against these instead of `null`. When adding a method to an interface, also implement it in its `*Null` class.
- **Focused interfaces (ISP)**: some interfaces, such as `Host` and `Vm`, grew too large by exposing methods that belong to the objects they hold. Keep interfaces small and cohesive: put a method in the interface of the object that owns the data, and let callers reach it through that object (e.g. `host.getProcessor().getFreeCores()` instead of `host.getFreePesNumber()`). Don't add delegating methods to `Host`/`Vm` just as shortcuts, and don't add methods to an interface that only some implementations need (e.g. CPU caps live in `CpuCap`, not in `Processor`, since a Host never has a cap).
- **Listeners**: entities expose `addOn...Listener` methods (e.g. `Vm.addOnHostAllocationListener`, `Cloudlet.addOnFinishListener`, `CloudSimPlus.addOnClockTickListener`) that receive typed `*EventInfo` objects from the `listeners` package.
- **Builders**: `builders` has fluent scenario builders and `builders.tables` has result table printers (`CloudletsTableBuilder`, text/CSV/Markdown/HTML/LaTeX tables).

### Packages not obvious from the names above

| Package | Purpose |
|---------|---------|
| `faultinjection` | `HostFaultInjection` entity that fails Host CPU cores at random times; `VmCloner` to recover VMs |
| `slametrics` | SLA contracts loaded from JSON |
| `vmtemplates` | AWS EC2 instance templates loaded from JSON |
| `traces` | Readers for Google Cluster Data, SWF and other workload traces |
| `testbeds` | Base classes to run repeated experiments and compute confidence intervals |
| `heuristics` | Generic heuristics (e.g. Simulated Annealing) used by `DatacenterBrokerHeuristic` |
| `util` | `Log` (Logback level control), time/byte/power conversions, `ResourceLoader` |

## Performance

Time and space complexity are crucial. CloudSim Plus is used for large-scale simulations with thousands of hosts and VMs and millions of cloudlets, so a small cost per object or per event quickly adds up.

**Always consider time and space complexity when proposing a change**, and state its impact in the proposal (such as the cost per cloudlet update or the memory per object). In particular:

- The processing update chain (Datacenter → Host → Vm → CloudletScheduler) runs for every cloudlet at each update. Avoid adding loops, lookups or object creation to it.
- Avoid adding lists, maps or sets to objects that exist in large numbers (such as Cloudlets, VMs, Hosts and their schedulers), mainly when the collection only grows. Prefer a flag or a value computed from existing state.
- Each scheduled event adds processing time. Avoid scheduling extra events when an existing one can be reused.
- If a feature has a cost, prefer a design where it's only paid by simulations that use it.

## Code Style

- **Java 25** with Lombok (`@Getter`, `@Setter`, `@NonNull`, etc.).
- `lombok.config`: builder class is named `Builder`, setters are chained (return `this`), and generated accessors are `final`.
- 4-space indentation, LF line endings (see `.editorconfig`).
- `checkstyle.xml` (IDE only) sets limits such as max 30 executable statements per method, boolean expression complexity of 3, nesting depth, method count.
- GPLv3 license header required on source files under `org/cloudsimplus` (`mvn license:format` adds it). Some files derived from the original CloudSim keep their original copyright and are tagged `@since CloudSim Plus x.y`.

## Testing

- **JUnit 5** (Jupiter) + **Mockito**.
- Unit tests mirror the main source structure under `src/test/java/org/cloudsimplus/`.
- Integration tests in `src/test/java/org/cloudsimplus/integrationtests/` run full simulations and only execute with `-Pintegration-tests`.
- Mocking helpers in `src/test/java/org/cloudsimplus/mocks/` (`CloudSimMocker`, `MocksHelper`) create mocked `Simulation` objects with a controlled clock.

### Run related examples before pushing

After changing code, check if there is a related example in the [cloudsimplus-examples](https://github.com/cloudsimplus/cloudsimplus-examples) project (usually cloned next to this one, at `../cloudsimplus-examples`), such as an example that uses the changed classes. If there is one, run it locally **before pushing**:

```bash
# Install the changed framework in the local Maven repository.
# The examples depend on the same SNAPSHOT version, so they use this build.
mvn clean install -DskipTests

# Rebuild the examples jar, so it includes the framework just installed.
# Don't rely on `bootstrap.sh <class>` alone: it only builds the jar if it doesn't exist,
# so it may run an old jar with the previous framework code.
cd ../cloudsimplus-examples
mvn clean package -DskipTests

# Run the example (some examples are in the org.cloudbus.cloudsim.examples package)
java -cp target/cloudsimplus-examples-*-with-dependencies.jar org.cloudsimplus.examples.<package>.<ExampleClassName>
```

Check that the example finishes without exceptions and that its results still make sense. If the change may affect the results, also run the example against `master` and compare the outputs. If there is no related example, say so.
