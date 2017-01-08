# CloudSim Plus Documentation Builder

This directory contains configuration files to generate CloudSim Plus documentation using [Sphinx](http://sphinx-doc.org),
in order to provide not just javadoc documentation, but complete user guides to be published at [ReadTheDocs site](http://cloudsimplus.readthedocs.io)
to provide pretty-looking, versioned and searcheable documentation.

To generate the Sphinx documentation to publish at the ReadTheDocs, considering you have python installed, you can execute the commands below to install the additional tools:

```shell
#Install pip to download python packages (can also be installed via package managers in Linux and macOS)
sudo curl https://bootstrap.pypa.io/get-pip.py | python

#Install sphinx and its build tools to converte rst documents to html, epub and other formats
pip install sphinx sphinx-autobuild 

#Install a Sphinx extension to parse javadocs comments inside Java files and generate rst files for Sphinx
pip install javasphinx-apidoc
```

To generate the rst files from the javadoc comments inside the Java files use:

```shell
make javadoc
```

To build the documentation in html use:
```shell
make html
```
