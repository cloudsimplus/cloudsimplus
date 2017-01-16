/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.sla.readJsonFile;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudsimplus.migration.VmMigrationWhenCpuMetricIsViolatedExample;

/**
 * This class read the sla agreements in json format.
 * 
 * The sla agreements is in the {@link SlaMetricDimension}. This class 
 * contains the name of the metric, the minimum and maximum 
 * acceptable value, and the metric unit. 
 * The minimum and maximum values will be used to check 
 * the violation of the metric. If the simulation metric is
 * not within these limits, it is violated and actions taken.
 * 
 *
 * @author raysaoliveira
 */
public class SlaReader {
    
    private final SlaContract contract;


    public SlaReader(String slaFileName) throws FileNotFoundException{
        Gson gson = new Gson();
        this.contract = gson.fromJson(
                new FileReader(slaFileName), SlaContract.class);
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        final String file = ResourceLoader.getResourcePath(VmMigrationWhenCpuMetricIsViolatedExample.class, "SlaMetrics.json");
        SlaReader reader = new SlaReader(file);
        for(SlaMetric m: reader.getContract().getMetrics()){
            System.out.println(m);
        }
        
        if(reader.getContract().getMetrics().isEmpty()){
            System.out.println("No metrics found");
        }
    }

    /**
     * @return the contract
     */
    public SlaContract getContract() {
        return contract;
    }
    


}
