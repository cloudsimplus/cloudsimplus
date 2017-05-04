/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

/**
 *
 * @author raysaoliveira
 */
public class SlaMetricsMonitoring {
    private String metric;

    public void monitoringTaskTimeCompletion(String metric){
        this.metric = metric; 
        System.out.println("\n-->The metric: " + metric + " was violated !!");
    }
    
    public void monitoringCpuUtilization(String metric){
        this.metric = metric; 
        System.out.println("\n-->The metric: " + metric + " was violated !!");
    }
    
    public void monitoringWaitTime(String metric){
        this.metric = metric; 
        System.out.println("\n-->The metric: " + metric + " was violated !!");
    }
    
}
