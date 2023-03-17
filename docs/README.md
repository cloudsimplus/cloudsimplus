# CloudSim Plus Documentation

This directory contains the sources to generate several different kinds of CloudSim Plus documentation. 
You can browse the [online documentation here](http://cloudsimplus.rtfd.io). 

The documentation includes:

- FAQs and guides in [reStructuredText (rst)](https://en.wikipedia.org/wiki/ReStructuredText) format to be published at [ReadTheDocs site](http://cloudsimplus.rtfd.io);
- [StarUML](http://staruml.io) project containing [UML diagrams](cloudsimplus.staruml.mdj); 
- [Side-by-Side comparison between CloudSim and CloudSim Plus java simulation scenarios](CloudSim-and-CloudSimPlus-Comparison.html) (online version available [here](http://cloudsimplus.org/CloudSim-and-CloudSimPlus-Comparison.html));
- [White Paper](https://github.com/cloudsimplus/cloudsimplus-whitepaper) published at the [EU/Brasil Cloud FORUM](https://eubrasilcloudforum.eu).
- [Web Slides](presentation/index.html) presenting CloudSim Plus (online version available [here](http://cloudsimplus.org/presentation/)).

## Building the reStructuredText files for the Documentation Site
To generate the [Sphinx](http://sphinx-doc.org) documentation to read locally, considering you have python installed, you can execute `make install` to install the additional tools.

Ensure you have the following environments variables declared, since the python scripts use UTF-8 encoding:

```bash
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
```

To build the documentation in html use:
```shell
make html
```

After that, you can navigate the generated documentation locally by accessing the `_build` directory. 
To publish the updated documentation to ReadTheDocs site, just commit any changes (which don't include the _build directory) and push them to GitHub.
