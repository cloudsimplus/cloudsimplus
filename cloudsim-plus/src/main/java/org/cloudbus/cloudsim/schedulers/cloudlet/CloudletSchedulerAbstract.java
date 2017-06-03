/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.PacketScheduler;
import org.cloudbus.cloudsim.util.Conversion;

import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;

import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Implements the basic features of a {@link CloudletScheduler}, representing
 * the policy of scheduling performed by a virtual machine to run its
 * {@link Cloudlet Cloudlets}. So, classes extending this must execute
 * Cloudlets. The interface for cloudlet management is also implemented in this
 * class. Each VM has to have its own instance of a CloudletScheduler.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public abstract class CloudletSchedulerAbstract implements CloudletScheduler {
    /**
     * @see #getCloudletPausedList()
     */
    private final List<CloudletExecutionInfo> cloudletPausedList;
    /**
     * @see #getCloudletFinishedList()
     */
    private final List<CloudletExecutionInfo> cloudletFinishedList;
    /**
     * @see #getCloudletFailedList()
     */
    private final List<CloudletExecutionInfo> cloudletFailedList;
    /**
     * @see #getPacketScheduler()
     */
    private PacketScheduler packetScheduler;
    /**
     * @see #getUsedPes()
     */
    private int usedPes;
    /**
     * @see #getPreviousTime()
     */
    private double previousTime;
    /**
     * @see #getCurrentMipsShare()
     */
    private List<Double> currentMipsShare;
    /**
     * @see #getCloudletExecList()
     */
    private List<CloudletExecutionInfo> cloudletExecList;
    /**
     * @see #getCloudletWaitingList()
     */
    private List<CloudletExecutionInfo> cloudletWaitingList;

    /**
     * @see #getVm()
     */
    private Vm vm;

    /**
     * @see #getCloudletReturnedList()
     */
    private Set<Cloudlet> cloudletReturnedList;

    /**
     * Creates a new CloudletScheduler object. A CloudletScheduler must be
     * created before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerAbstract() {
        setPreviousTime(0.0);
        usedPes = 0;
        vm = Vm.NULL;
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList = new ArrayList<>();
        cloudletWaitingList = new ArrayList<>();
        cloudletReturnedList = new HashSet<>();
        currentMipsShare = new ArrayList<>();
        packetScheduler = PacketScheduler.NULL;
    }

    @Override
    public double getPreviousTime() {
        return previousTime;
    }

    /**
     * Sets the previous time when the scheduler updated the processing of
     * cloudlets it is managing.
     *
     * @param previousTime the new previous time
     */
    protected final void setPreviousTime(double previousTime) {
        this.previousTime = previousTime;
    }

    @Override
    public List<Double> getCurrentMipsShare() {
        return Collections.unmodifiableList(currentMipsShare);
    }

    /**
     * Sets the list of current mips share available for the VM using the
     * scheduler.
     *
     * @param currentMipsShare the new current mips share
     * @see #getCurrentMipsShare()
     */
    protected void setCurrentMipsShare(List<Double> currentMipsShare) {
        if(currentMipsShare.size() > vm.getNumberOfPes()){
            Log.printFormattedLine("Requested %d PEs but %s has just %d", currentMipsShare.size(), vm, vm.getNumberOfPes());
        }
        this.currentMipsShare = currentMipsShare;
    }


    /**
     * Gets the amount of MIPS available (free) for each Processor PE,
     * considering the currently executing cloudlets in this processor
     * and the number of PEs these cloudlets require.
     * This is the amount of MIPS that each Cloudlet is allowed to used,
     * considering that the processor is shared among all executing
     * cloudlets.
     *
     * <p>In the case of space shared schedulers,
     * there is no concurrency for PEs because some cloudlets
     * may wait in a queue until there is available PEs to be used
     * exclusively by them.</p>
     *
     * @return the amount of available MIPS for each Processor PE.
     * @TODO Splitting the capacity of a CPU core among different applications
     * is not in fact possible. This was just an oversimplification
     * performed by the CloudletSchedulerTimeShared that may affect
     * other schedulers such as the CloudletSchedulerCompletelyFair
     * that in fact performs tasks preemption.
     */
    public double getAvailableMipsByPe(){
        final long totalPesOfAllExecCloudlets = totalPesOfAllExecCloudlets();
        if(totalPesOfAllExecCloudlets > currentMipsShare.size()) {
            return getTotalMipsShare() / totalPesOfAllExecCloudlets;
        }

        return getPeCapacity();
    }

    private Double getPeCapacity() {
        return currentMipsShare.stream().findFirst().orElse(0.0);
    }

    /**
     * Gets the total number of PEs of all cloudlets currently executing in this processor.
     * @return
     */
    private long totalPesOfAllExecCloudlets() {
        return cloudletExecList.stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .mapToLong(Cloudlet::getNumberOfPes).sum();
    }

    private double getTotalMipsShare(){
        return currentMipsShare.stream().reduce(0.0, Double::sum);
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletExecList() {
        return Collections.unmodifiableList(cloudletExecList);
    }

    protected void addCloudletToWaitingList(CloudletExecutionInfo cloudlet) {
        if(cloudlet == null || CloudletExecutionInfo.NULL.equals(cloudlet)){
            return;
        }

        cloudlet.setCloudletStatus(Cloudlet.Status.QUEUED);
        cloudletWaitingList.add(cloudlet);
    }


    /**
     * Gets the list of paused cloudlets.
     *
     * @return the cloudlet paused list
     */
    protected List<CloudletExecutionInfo> getCloudletPausedList() {
        return cloudletPausedList;
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletFinishedList() {
        return cloudletFinishedList;
    }

    protected void addCloudletToFinishedList(CloudletExecutionInfo cloudlet) {
        cloudletFinishedList.add(cloudlet);
    }

    /**
     * Gets the list of failed cloudlets.
     *
     * @return the cloudlet failed list.
     */
    protected List<CloudletExecutionInfo> getCloudletFailedList() {
        return cloudletFailedList;
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletWaitingList() {
        return Collections.unmodifiableList(cloudletWaitingList);
    }

    /**
     * Sorts the {@link #cloudletWaitingList} using a given {@link Comparator}.
     * @param comparator the {@link Comparator} to sort the Waiting Cloudlets List
     */
    protected void sortCloudletWaitingList(Comparator<CloudletExecutionInfo> comparator){
        cloudletWaitingList.sort(comparator);
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

    @Override
    public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
        return processCloudletSubmit(new CloudletExecutionInfo(cl), fileTransferTime);
    }

    /**
     * Process a Cloudlet after it is received by the
     * {@link #cloudletSubmit(Cloudlet, double)} method, that creates a
     * {@link CloudletExecutionInfo} object to encapsulate the submitted
     * Cloudlet and record execution information.
     *
     * @param rcl              the CloudletExecutionInfo that encapsulates the Cloudlet
     *                         object
     * @param fileTransferTime time required to move the required files from the
     *                         SAN to the VM
     * @return expected finish time of this cloudlet (considering the time to
     * transfer required files from the Datacenter to the Vm), or 0 if it is in
     * a waiting queue
     */
    protected double processCloudletSubmit(CloudletExecutionInfo rcl, double fileTransferTime) {
        if (canAddCloudletToExecutionList(rcl)) {
            rcl.setCloudletStatus(Status.INEXEC);
            rcl.setFileTransferTime(fileTransferTime);
            addCloudletToExecList(rcl);
            return fileTransferTime + (rcl.getCloudletLength() / getPeCapacity());
        }

        // No enough free PEs, then add Cloudlet to the waiting queue
        addCloudletToWaitingList(rcl);
        return 0.0;
    }

    /**
     * Adds a Cloudlet to the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be added
     */
    protected void addCloudletToExecList(CloudletExecutionInfo cloudlet) {
        cloudlet.setCloudletStatus(Cloudlet.Status.INEXEC);
        cloudlet.setLastProcessingTime(getVm().getSimulation().clock());
        cloudletExecList.add(cloudlet);
        addUsedPes(cloudlet.getNumberOfPes());
    }

    @Override
    public boolean hasFinishedCloudlets() {
        return !cloudletFinishedList.isEmpty();
    }

    @Override
    public int runningCloudletsNumber() {
        return cloudletExecList.size();
    }

    /**
     * Returns the first cloudlet in the execution list to migrate to another
     * VM, removing it from the list.
     *
     * @return the first executing cloudlet or {@link Cloudlet#NULL} if the
     * executing list is empty
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet getCloudletToMigrate() {
        Function<CloudletExecutionInfo, Cloudlet> finishMigratingCloudlet = rcl -> {
            removeCloudletFromExecListAndAddToFinishedList(rcl);
            rcl.finalizeCloudlet();
            return rcl.getCloudlet();
        };

        return cloudletExecList.stream()
            .findFirst()
            .map(finishMigratingCloudlet).orElse(Cloudlet.NULL);
    }

    @Override
    public int getCloudletStatus(int cloudletId) {
        final Optional<CloudletExecutionInfo> optional = findCloudletInAllLists(cloudletId);
        return optional
            .map(CloudletExecutionInfo::getCloudlet)
            .map(Cloudlet::getStatus)
            .map(Status::ordinal)
            .orElse(-1);
    }

    /**
     * Search for a Cloudlet into all Cloudlet lists.
     *
     * @param cloudletId the id of the Cloudlet to search for
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecutionInfo> findCloudletInAllLists(final double cloudletId) {
        //Concatenate all lists into a strem
        final Stream<List<CloudletExecutionInfo>> streamOfAllLists
            = Stream.of(cloudletExecList, cloudletPausedList, cloudletWaitingList,
            cloudletFinishedList, cloudletFailedList);
        //Gets all elements in each list and makes them a single full list,
        //returning the first Cloudlet with the given id
        return streamOfAllLists
            .flatMap(List::stream)
            .filter(c -> c.getCloudletId() == cloudletId)
            .findFirst();
    }

    /**
     * Search for a Cloudlet into a given list.
     *
     * @param cloudletId the id of the Cloudlet to search for
     * @param list       the list to search the Cloudlet into
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecutionInfo> findCloudletInList(double cloudletId, List<CloudletExecutionInfo> list) {
        return list.stream()
            .filter(rcl -> rcl.getCloudletId() == cloudletId)
            .findFirst();
    }

    @Override
    public void cloudletFinish(CloudletExecutionInfo rcl) {
        rcl.setCloudletStatus(Cloudlet.Status.SUCCESS);
        rcl.finalizeCloudlet();
        addCloudletToFinishedList(rcl);
    }

    @Override
    public boolean cloudletPause(int cloudletId) {
        if (changeStatusOfCloudletIntoList(
            cloudletExecList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.PAUSED)) != Cloudlet.NULL) {
            return true;
        }

        return changeStatusOfCloudletIntoList(
            cloudletWaitingList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.READY, Status.PAUSED)) != Cloudlet.NULL;
    }

    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        Cloudlet cloudlet;
        cloudlet = changeStatusOfCloudletIntoList(cloudletFinishedList, cloudletId, (c) -> {
        });
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
            cloudletExecList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
            cloudletPausedList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.PAUSED, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
            cloudletWaitingList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.READY, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        return Cloudlet.NULL;
    }

    /**
     * Changes the status of a given cloudlet.
     *
     * @param cloudlet      Cloudlet to set its status
     * @param currentStatus the current cloudlet status
     * @param newStatus     the new status to set
     * @todo @author manoelcampos The parameter currentStatus only exists
     * because apparently, the cloudlet status is not being accordingly changed
     * along the simulation run.
     */
    private void changeStatusOfCloudlet(CloudletExecutionInfo cloudlet, Status currentStatus, Status newStatus) {
        if ((currentStatus == Status.INEXEC || currentStatus == Status.READY) && cloudlet.getCloudlet().isFinished()) {
            cloudletFinish(cloudlet);
        } else {
            cloudlet.setCloudletStatus(newStatus);
        }

        switch (newStatus) {
            case PAUSED:
                cloudletPausedList.add(cloudlet);
                break;
        }
    }

    /**
     * Search for a cloudlet into a given list in order to change its status and
     * remove it from that list.
     *
     * @param cloudletList                  the list where to search the cloudlet
     * @param cloudletId                    the id of the cloudlet to have its status changed
     * @param cloudletStatusUpdaterConsumer the {@link Consumer} that will apply
     *                                      the change in the status of the found cloudlet
     * @return the changed cloudlet or {@link Cloudlet#NULL} if not found in the
     * given list
     */
    private Cloudlet changeStatusOfCloudletIntoList(
        List<CloudletExecutionInfo> cloudletList, int cloudletId,
        Consumer<CloudletExecutionInfo> cloudletStatusUpdaterConsumer)
    {
        Function<CloudletExecutionInfo, Cloudlet> removeCloudletFromListAndUpdateItsStatus = c -> {
            cloudletList.remove(c);
            cloudletStatusUpdaterConsumer.accept(c);
            return c.getCloudlet();
        };

        return cloudletList.stream()
            .filter(c -> c.getCloudlet().getId() == cloudletId)
            .findFirst()
            .map(removeCloudletFromListAndUpdateItsStatus)
            .orElse(Cloudlet.NULL);
    }

    @Override
    public double updateProcessing(double currentTime, List<Double> mipsShare) {
        setCurrentMipsShare(mipsShare);

        // no more cloudlets in this scheduler
        if (cloudletExecList.isEmpty() && cloudletWaitingList.isEmpty()) {
            setPreviousTime(currentTime);
            return Double.MAX_VALUE;
        }

        updateCloudletsProcessing(currentTime);
        updateVmRamAbsoluteUtilization();
        removeFinishedCloudletsFromExecutionListAndAddToFinishedList();
        moveNextCloudletsFromWaitingToExecList();

        final double nextSimulationTime = getEstimatedFinishTimeOfSoonerFinishingCloudlet(currentTime);
        setPreviousTime(currentTime);

        return nextSimulationTime;
    }

    /**
     * Updates the processing of all cloudlets of the Vm using this scheduler
     * that are in the {@link #getCloudletExecList() cloudlet execution list}.
     *
     * @param currentTime current simulation time
     */
    private void updateCloudletsProcessing(double currentTime) {
        cloudletExecList.forEach(rcl -> updateCloudletProcessingAndPacketsDispatch(rcl, currentTime));
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler and packets that such a Cloudlet has to send or to receive
     * (if the CloudletScheduler has a {@link PacketScheduler} assigned to it).
     *
     * @param rcl         The cloudlet to be its processing updated
     * @param currentTime current simulation time
     */
    private void updateCloudletProcessingAndPacketsDispatch(CloudletExecutionInfo rcl, double currentTime) {
        if (packetScheduler.isTimeToUpdateCloudletProcessing(rcl.getCloudlet())) {
            updateCloudletProcessing(rcl, currentTime);
        }

        packetScheduler.processCloudletPackets(rcl.getCloudlet(), currentTime);
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler.
     *
     * @param rcl The cloudlet to be its processing updated
     * @param currentTime current simulation time
     */
    protected void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {
        final long executedInstructions = cloudletExecutedInstructionsForElapsedTime(rcl, currentTime);
        rcl.updateProcessing(executedInstructions);
        if (executedInstructions > 0) {
            rcl.setLastProcessingTime(currentTime);
        }
    }

    /**
     * Updates the VM usage of RAM, based on the current utilization of all
     * its running Cloudlets, that depends on the {@link Cloudlet#getUtilizationModelRam()}.
     */
    private void updateVmRamAbsoluteUtilization() {
        final ResourceManageable ram = vm.getResource(Ram.class);
        final double totalUsedRam = cloudletExecList.stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .mapToDouble(this::getCloudletRamAbsoluteUtilization)
            .sum();

        ram.setAllocatedResource(totalUsedRam);
    }

    /**
     * Gets the absolute value of RAM utilization for a given Cloudlet
     *
     * @param cloudlet the Cloudlet to get the absolute value of RAM utilization
     * @return the Cloudlet RAM utilization in absolute value
     */
    private double getCloudletRamAbsoluteUtilization(Cloudlet cloudlet) {
        final ResourceManageable ram = vm.getResource(Ram.class);
        final UtilizationModel um = cloudlet.getUtilizationModelRam();
        final double utilization = um.getUnit() == Unit.ABSOLUTE ?
            Math.min(um.getUtilization(), vm.getRam().getCapacity()) :
            um.getUtilization() * ram.getCapacity();
        return utilization;
    }

    /**
     * Computes the length of a given cloudlet, in number
     * of Instructions (I), which has been executed since the last time cloudlet
     * processing was updated.
     *
     * <p>
     * This method considers the delay for actually starting the Cloudlet
     * execution due to the time to transfer
     * {@link Cloudlet#getRequiredFiles() required Cloudlet files} from the
     * Datacenter storage (such as a SAN) to the Vm running the Cloudlet.</p>
     *
     * <p>
     * During this transfer time, the method will always return 0 to indicate
     * that the Cloudlet was not processed in fact, it is just waiting the
     * required files to be acquired. The required time to transfer the files is
     * stored in the {@link CloudletExecutionInfo#getFileTransferTime()}
     * attribute and is set when the Cloudlet is submitted to the scheduler.</p>
     *
     * @param rcl the Cloudlet to compute the executed length
     * @param currentTime current simulation time
     * @return the executed length, in number of Instructions (I), since the last time cloudlet was processed.
     * @TODO @author manoelcampos This method is being called 2 times more than
     * required. Despite it is not causing any apparent issue, it has to be
     * investigated. For instance, for simulation time 2, with 2 cloudlets, the
     * method is being called 4 times instead of just 2 (1 for each cloudlet for
     * that time).
     * @see #updateCloudletsProcessing(double)
     */
    protected long cloudletExecutedInstructionsForElapsedTime(CloudletExecutionInfo rcl, double currentTime) {
        /* The time the Cloudlet spent executing in fact, since the last time Cloudlet update was
         * called by the scheduler. If it is zero, indicates that the Cloudlet didn't use
         * the CPU in this time span, because it is waiting for its required files
         * to be acquired from the Datacenter storage.
         */
        final double actualProcessingTime = (hasCloudletFileTransferTimePassed(rcl, currentTime) ? timeSpan(currentTime) : 0);
        final double cloudletUsedMips =
            getAbsoluteCloudletResourceUtilization(rcl.getCloudlet().getUtilizationModelCpu(),
                currentTime, getAvailableMipsByPe());
        return (long) (cloudletUsedMips * actualProcessingTime * Conversion.MILLION);
    }

    /**
     * Checks if the time to transfer the files required by a Cloudlet to
     * execute has already passed, in order to start executing the Cloudlet in
     * fact.
     *
     * @param rcl         Cloudlet to check if the time to transfer the files has passed
     * @param currentTime the current simulation time
     * @return true if the time to transfer the files has passed, false
     * otherwise
     */
    private boolean hasCloudletFileTransferTimePassed(CloudletExecutionInfo rcl, double currentTime) {
        return rcl.getFileTransferTime() == 0
            || currentTime - rcl.getLastProcessingTime() > rcl.getFileTransferTime()
            || rcl.getCloudlet().getFinishedLengthSoFar() > 0;
    }

    /**
     * Computes the time span between the current simulation time and the last
     * time the scheduler updated the processing of it's managed cloudlets. The
     * method manages to correct precision issues of double values math
     * operations.
     *
     * @param currentTime the current simulation time
     * @return
     */
    protected double timeSpan(double currentTime) {
        return Math.floor(currentTime) - Math.floor(previousTime);
    }

    /**
     * Removes finished cloudlets from the
     * {@link #getCloudletExecList() list of cloudlets to execute}
     * and adds them to finished list.
     *
     * @return the number of finished cloudlets removed from the
     * {@link #getCloudletExecList() execution list}
     */
    private int removeFinishedCloudletsFromExecutionListAndAddToFinishedList() {
        final List<CloudletExecutionInfo> finishedCloudlets
            = cloudletExecList.stream()
            .filter(c -> c.getCloudlet().isFinished())
            .collect(toList());

        for (final CloudletExecutionInfo c : finishedCloudlets) {
            removeCloudletFromExecListAndAddToFinishedList(c);
        }

        return finishedCloudlets.size();
    }

    private void removeCloudletFromExecListAndAddToFinishedList(CloudletExecutionInfo cloudlet) {
        setCloudletFinishTimeAndAddToFinishedList(cloudlet);
        removeCloudletFromExecList(cloudlet);
    }

    /**
     * Removes a Cloudlet from the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be removed
     * @return the removed Cloudlet or {@link CloudletExecutionInfo#NULL} if not found
     */
    protected CloudletExecutionInfo removeCloudletFromExecList(CloudletExecutionInfo cloudlet) {
        removeUsedPes(cloudlet.getNumberOfPes());
        return cloudletExecList.remove(cloudlet) ? cloudlet : CloudletExecutionInfo.NULL;
    }

    /**
     * Sets the finish time of a cloudlet and adds it to the
     * finished list.
     *
     * @param rcl the cloudlet to set the finish time
     */
    private void setCloudletFinishTimeAndAddToFinishedList(CloudletExecutionInfo rcl) {
        final double clock = vm.getSimulation().clock();
        rcl.setFinishTime(clock);
        cloudletFinish(rcl);
    }

    /**
     * Gets the estimated time, considering the current time, that a next Cloudlet is expected to finish.
     *
     * @param currentTime current simulation time
     * @return the estimated finish time of sooner finishing cloudlet
     * (which is a relative delay from the current simulation time)
     */
    protected double getEstimatedFinishTimeOfSoonerFinishingCloudlet(final double currentTime) {
        return cloudletExecList
            .stream()
            .mapToDouble(c -> getEstimatedFinishTimeOfCloudlet(c, currentTime))
            .min().orElse(Double.MAX_VALUE);
    }

    /**
     * Gets the estimated time when a given cloudlet is supposed to finish
     * executing. It considers the amount of Vm PES and the sum of PEs required
     * by all VMs running inside the VM.
     *
     * @param rcl         cloudlet to get the estimated finish time
     * @param currentTime current simulation time
     * @return the estimated finish time of the given cloudlet
     * (which is a relative delay from the current simulation time)
     */
    protected double getEstimatedFinishTimeOfCloudlet(CloudletExecutionInfo rcl, final double currentTime) {
        final double cloudletUsedMips =
            getAbsoluteCloudletResourceUtilization(rcl.getCloudlet().getUtilizationModelCpu(),
                currentTime, getAvailableMipsByPe());
        double estimatedFinishTime = rcl.getRemainingCloudletLength() / cloudletUsedMips;

        if (estimatedFinishTime < vm.getSimulation().getMinTimeBetweenEvents()) {
            estimatedFinishTime = vm.getSimulation().getMinTimeBetweenEvents();
        }

        return estimatedFinishTime;
    }

    /**
     * Selects the next Cloudlets in the waiting list to move to the execution
     * list in order to start executing them. While there is enough free PEs,
     * the method try to find a suitable Cloudlet in the list, until it reaches
     * the end of such a list.
     *
     * <p>
     * The method might also exchange some cloudlets in the execution list with
     * some in the waiting list. Thus, some running cloudlets may be preempted
     * to give opportunity to previously waiting cloudlets to run. This is a
     * process called
     * <a href="https://en.wikipedia.org/wiki/Context_switch">context switch</a>.
     * However, each CloudletScheduler implementation decides how
     * such a process is implemented. For instance, Space-Shared schedulers may
     * just perform context switch just after currently running Cloudlets
     * completely finish executing.
     * <p>
     * <p>
     * This method is called internally by the
     * {@link CloudletScheduler#updateProcessing(double, List)} one.</p>
     *
     * @pre currentTime >= 0
     * @post $none
     */
    protected void moveNextCloudletsFromWaitingToExecList() {
        Optional<CloudletExecutionInfo> optional = Optional.of(CloudletExecutionInfo.NULL);
        while (!cloudletWaitingList.isEmpty() && optional.isPresent() && getFreePes() > 0) {
            optional = findSuitableWaitingCloudlet();
            optional.ifPresent(this::addWaitingCloudletToExecList);
        }
    }

    /**
     * Try to find the first Cloudlet in the waiting list which the number of
     * required PEs is not higher than the number of free PEs.
     *
     * @return an {@link Optional} containing the found Cloudlet or an empty
     * Optional otherwise
     */
    protected Optional<CloudletExecutionInfo> findSuitableWaitingCloudlet() {
        return cloudletWaitingList.stream()
            .filter(this::isThereEnoughFreePesForCloudlet)
            .findFirst();
    }

    /**
     * Checks if the amount of PEs required by a given Cloudlet is free to use.
     *
     * @param c the Cloudlet to get the number of required PEs
     * @return true if there is the amount of free PEs, false otherwise
     */
    protected boolean isThereEnoughFreePesForCloudlet(CloudletExecutionInfo c) {
        return currentMipsShare.size() - usedPes >= c.getNumberOfPes();
    }

    /**
     * Removes a Cloudlet from waiting list and adds it to the exec list.
     * @param cloudlet the cloudlet to add to to exec list
     * @return the given cloudlet
     */
    protected CloudletExecutionInfo addWaitingCloudletToExecList(CloudletExecutionInfo cloudlet) {
        /*If the Cloudlet is not found in the waiting List, there is no problem.
        * Just add it to the exec List.*/
        cloudletWaitingList.remove(cloudlet);
        addCloudletToExecList(cloudlet);
        return cloudlet;
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public void setVm(Vm vm) {
        Objects.requireNonNull(vm);

        if (isOtherVmAssigned(vm)) {
            throw new IllegalArgumentException(
                "CloudletScheduler already has a Vm assigned to it. Each Vm must have its own CloudletScheduler instance.");
        }

        this.vm = vm;
    }

    /**
     * Checks if the {@link CloudletScheduler} has a {@link Vm} assigned that is
     * different from the given one
     *
     * @param vm the Vm to check if assigned scheduler's Vm is different from
     * @return
     */
    private boolean isOtherVmAssigned(Vm vm) {
        return !Objects.isNull(this.vm) && this.vm != Vm.NULL && !vm.equals(this.vm);
    }

    @Override
    public long getUsedPes() {
        /**
         *
         * @todo The number of free and used PEs should be inside the Processor
         * class. However, a new instance of the class is created every time the
         * updateVmProcessing is called, what will make the information about
         * the number of usedPes to be lost.
         */
        return usedPes;
    }

    /**
     * Gets the number of PEs currently not being used.
     *
     * @return
     */
    @Override
    public long getFreePes() {
        return currentMipsShare.size() - usedPes;
    }

    /**
     * Adds a given number of PEs to the amount of currently used PEs.
     *
     * @param usedPesToAdd number of PEs to add to the amount of used PEs
     */
    private void addUsedPes(long usedPesToAdd) {
        this.usedPes += usedPesToAdd;
    }

    /**
     * Subtracts a given number of PEs from the amount of currently used PEs.
     *
     * @param usedPesToRemove number of PEs to subtract from the amount of used
     *                        PEs
     */
    private void removeUsedPes(long usedPesToRemove) {
        this.usedPes = (int)Math.max(0, this.usedPes-usedPesToRemove);
    }

    @Override
    public PacketScheduler getPacketScheduler() {
        return packetScheduler;
    }

    @Override
    public void setPacketScheduler(PacketScheduler packetScheduler) {
        this.packetScheduler = (Objects.isNull(packetScheduler) ? PacketScheduler.NULL : packetScheduler);
        this.packetScheduler.setVm(vm);
    }

    @Override
    public boolean isTherePacketScheduler() {
        return !(Objects.isNull(packetScheduler) || packetScheduler == PacketScheduler.NULL);
    }

    @Override
    public double getRequestedCpuPercentUtilization(double time) {
        return cloudletExecList.stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .mapToDouble(c -> getAbsoluteCloudletCpuUtilizationForAllPes(time, c))
            .sum() / vm.getTotalMipsCapacity();
    }

    /**
     * Gets the total CPU utilization in MIPS for a Cloudlet, considering
     * all the PEs in which it is running.
     *
     * @param time the simulation time
     * @param cloudlet the Cloudlet to get the total CPU utilization
     * @return the total Cloudlet CPU utilization (in MIPS) across all PEs it is using
     */
    private double getAbsoluteCloudletCpuUtilizationForAllPes(double time, Cloudlet cloudlet) {
        final double cloudletCpuUsageForOnePe =
            getAbsoluteCloudletResourceUtilization(
                cloudlet.getUtilizationModelCpu(), time, getAvailableMipsByPe());

        return cloudletCpuUsageForOnePe * cloudlet.getNumberOfPes();
    }

    @Override
    public double getRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        return getAbsoluteCloudletResourceUtilization(rcl.getCloudlet().getUtilizationModelCpu(), time, vm.getMips());
    }

    @Override
    public double getAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        return getAbsoluteCloudletResourceUtilization(rcl.getCloudlet().getUtilizationModelCpu(), time, getAvailableMipsByPe());
    }

    @Override
    public double getCurrentRequestedBwPercentUtilization() {
        return cloudletExecList.stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .map(Cloudlet::getUtilizationModelBw)
            .mapToDouble(um -> getAbsoluteCloudletResourceUtilization(um, vm.getBw().getCapacity()))
            .sum() / vm.getBw().getCapacity();
    }

    @Override
    public double getCurrentRequestedRamPercentUtilization() {
        return cloudletExecList.stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .map(Cloudlet::getUtilizationModelRam)
            .mapToDouble(um -> getAbsoluteCloudletResourceUtilization(um, vm.getRam().getCapacity()))
            .sum() / vm.getRam().getCapacity();
    }

    /**
     * Computes the absolute amount of a resource used by a given Cloudlet
     * for the current simulation time, based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param um                      the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param maxResourceAllowedToUse the maximum absolute resource that the Cloudlet will be allowed to use
     * @return the absolute amount of resource that the Cloudlet will use
     */
    private double getAbsoluteCloudletResourceUtilization(UtilizationModel um, double maxResourceAllowedToUse) {
        return getAbsoluteCloudletResourceUtilization(um, vm.getSimulation().clock(), maxResourceAllowedToUse);
    }

    /**
     * Computes the absolute amount of a resource used by a given Cloudlet
     * for a given time, based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param um                      the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param time                    the simulation time
     * @param maxResourceAllowedToUse the maximum absolute resource that the Cloudlet will be allowed to use
     * @return the absolute amount of resource that the Cloudlet will use
     */
    private double getAbsoluteCloudletResourceUtilization(UtilizationModel um, double time, double maxResourceAllowedToUse) {
        return um.getUnit() == Unit.ABSOLUTE ?
            Math.min(um.getUtilization(time), maxResourceAllowedToUse) :
            um.getUtilization() * maxResourceAllowedToUse;
    }

    @Override
    public Set<Cloudlet> getCloudletReturnedList() {
        return Collections.unmodifiableSet(cloudletReturnedList);
    }

    @Override
    public boolean isCloudletReturned(Cloudlet cloudlet) {
        return cloudletReturnedList.contains(cloudlet);
    }

    @Override
    public void addCloudletToReturnedList(Cloudlet cloudlet) {
        this.cloudletReturnedList.add(cloudlet);
    }

    @Override
    public void deallocatePesFromVm(Vm vm, int pesToRemove) {
        removeUsedPes(pesToRemove);
        deallocatePesFromMipsShare(pesToRemove);
    }

    private void deallocatePesFromMipsShare(int pesToRemove) {
        pesToRemove = Math.min(pesToRemove, currentMipsShare.size());
        IntStream.range(0, pesToRemove).forEach(i -> currentMipsShare.remove(0));
    }

    @Override
    public List<Cloudlet> getCloudletList() {
        return Stream.concat(cloudletExecList.stream(), cloudletWaitingList.stream())
                     .map(CloudletExecutionInfo::getCloudlet)
                     .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public boolean isEmpty() {
        return getCloudletList().isEmpty();
    }
}
