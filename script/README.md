# Utility Scripts

This directory contains some scripts to automate some tasks.

- [bootstrap.sh](bootstrap.sh): Allows you to build CloudSim Plus and execute its examples easily. It's a good way to try CloudSim Plus without relying on some IDE.
                                Just run the script without parameters to see how to use.
- [install.sh](install.sh): Allows you to build all CloudSim Plus modules using maven and install them into the local maven repository, running
                            all tests (including Integration Tests). It doesn't require any parameter.
- [asciidoc.sh](asciidoc.sh): An experimental script that exports javadocs comments inside java source files to AsciiDoc format. It doesn't require any parameter.
- [download-google-cluster-data.sh](download-google-cluster-data.sh): Downloads [Google Cluster Data](https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md) trace files to be used to create simulations. Execute the script with `-h` argument to show usage help.
                            
