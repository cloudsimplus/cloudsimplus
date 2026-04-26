# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CloudSim Plus is a discrete event simulation framework for cloud computing infrastructures (datacenters, hosts, VMs, cloudlets/tasks). It's a modern Java library published to Maven Central under `org.cloudsimplus:cloudsimplus`.

## Build Commands

```bash
# Build and run unit tests
mvn clean install

# Run all tests (unit + integration)
mvn clean verify -Pintegration-tests

# Run a single test class
mvn test -Dtest=VmSimpleTest

# Run a single test method
mvn test -Dtest=VmSimpleTest#testMethod

# Generate code coverage report
mvn test jacoco:report
```

Maven wrapper (`./mvnw`) is available for environments without Maven installed.

## Architecture

### Simulation Engine (core package)
The simulation runs as a discrete event loop in `CloudSimPlus` (implements `Simulation`). Entities (`SimEntity`) communicate by sending events through `SimEvent` objects managed by event queues (`FutureQueue`, `DeferredQueue`). The simulation clock advances to the next event time each iteration.

### Entity Hierarchy
The main simulation entities form a layered infrastructure:
- **Datacenter** — manages physical hosts and handles VM placement via `VmAllocationPolicy`
- **Host** — physical machine with PEs (processing elements/CPU cores), RAM, BW, storage; provisions resources to VMs via `ResourceProvisioner` classes
- **Vm** — virtual machine scheduled on a host; uses `CloudletScheduler` to manage its cloudlets
- **Cloudlet** — a task/application that executes on a VM, consuming resources over time per its `UtilizationModel`
- **DatacenterBroker** — acts as the customer agent, submitting VMs and cloudlets to datacenters

### Key Design Patterns
- **Sealed interfaces**: `Simulation`, `Vm`, `Host`, `Cloudlet`, `DatacenterBroker`, and others, are sealed to control the inheritance hierarchy
- **Null Object Pattern**: every major interface has a `NULL` constant (e.g., `Vm.NULL`, `Host.NULL`) — use these instead of null references
- **Strategy Pattern**: schedulers (`CloudletScheduler`, `VmScheduler`), allocation policies (`VmAllocationPolicy`), provisioners (`ResourceProvisioner`), and utilization models are all pluggable strategies
- **Builder Pattern**: `builders` package provides fluent builders for creating simulation scenarios
- **Listener/Observer**: `listeners` package with typed event info objects for monitoring entity state changes

### Package Map

| Package | Purpose |
|---------|---------|
| `core` | Simulation engine, events, entity base classes |
| `datacenters` | Datacenter implementations |
| `hosts` | Physical machine (Host) implementations |
| `vms` | Virtual machine implementations |
| `cloudlets` | Task/workload implementations |
| `brokers` | Customer broker agents |
| `schedulers` | VM and cloudlet schedulers |
| `allocationpolicies` | VM-to-host placement and migration |
| `provisioners` | Resource provisioning (PE, RAM, BW) |
| `resources` | Resource abstractions (Pe, Ram, Bandwidth, Storage) |
| `utilizationmodels` | Resource usage models over time |
| `power` | Power consumption modeling |
| `autoscaling` | Horizontal and vertical VM scaling |
| `network` | Network topology and communication |
| `distributions` | Probability distributions for stochastic models |
| `traces` | Workload trace readers (Google Cluster Data) |
| `listeners` | Event listener interfaces and info objects |
| `builders` | Fluent builders for simulation setup |
| `heuristics` | Optimization heuristics (Simulated Annealing, etc.) |
| `services` | Microservice interdependencies — `Service`, `ServiceCall` graphs, `ServiceRequest`, `ServiceBrokerSimple` for chained call simulation (e.g. `Request → A → B → A → D → E`) |

## Code Style

- **Java 25** with Lombok annotations (`@Getter`, `@Setter`, `@Builder`, etc.)
- **4-space indentation**, LF line endings (see `.editorconfig`)
- Checkstyle enforced (see `checkstyle.xml`): max 30 executable statements per method, max boolean complexity of 3, controlled nesting depth
- Lombok config: builder class named "Builder", accessor chaining enabled, all generated fields final
- GPLv3 license headers required on all source files (enforced by `license-maven-plugin`)

## Testing

- **JUnit 5** (Jupiter) + **Mockito** for mocking
- Unit tests mirror the main source structure under `src/test/java/org/cloudsimplus/`
- Integration tests live in `src/test/java/org/cloudsimplus/integrationtests/` (only run with `-Pintegration-tests` profile)
- Test mocking utilities in `src/test/java/org/cloudsimplus/mocks/` (`CloudSimMocker`, `MocksHelper`)
