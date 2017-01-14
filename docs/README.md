# CloudSim Plus Documentation Builder

This directory contains configuration files to generate CloudSim Plus documentation using [Sphinx](http://sphinx-doc.org),
in order to provide not just javadocs, but complete user guides published at [ReadTheDocs site](http://cloudsimplus.readthedocs.io)
in a pretty-looking, versioned and searcheable way. All scripts and instructions are for Linux and macOS operating systems.

To generate the Sphinx documentation to publish at the ReadTheDocs or to read locally, considering you have python installed, you can execute the commands below to install the additional tools:

```bash
#Install pip to download python packages (can also be installed via package managers in Linux and macOS)
sudo curl https://bootstrap.pypa.io/get-pip.py | python

#Install sphinx and its build tools to locally build the rst documents to html, latex, epub or other formats
pip install sphinx sphinx-autobuild 

#Install a Sphinx extension to parse javadocs comments inside Java files and generate rst files for Sphinx
pip install javasphinx-apidoc
```

Ensure that you have the following environments variables declared, since the python scripts use UTF-8 encoding:

```bash
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
```

To generate the rst files from the javadoc comments inside the Java source files use:

```shell
make javadoc
```

To build the documentation in html use:
```shell
make html
```
