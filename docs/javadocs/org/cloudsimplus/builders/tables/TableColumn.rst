TableColumn
===========

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public interface TableColumn

   An interface that represents a column of a table generated using a \ :java:ref:`Table`\ .

   :author: Manoel Campos da Silva Filho

Methods
-------
generateData
^^^^^^^^^^^^

.. java:method::  String generateData(Object data)
   :outertype: TableColumn

   Generates the string that represents the data of the column, formatted according to the \ :java:ref:`format <getFormat()>`\ .

   :param data: The data of the column to be formatted
   :return: a string containing the formatted column data

generateSubtitleHeader
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  String generateSubtitleHeader()
   :outertype: TableColumn

   Generates the string that represents the sub-header of the column (if any), containing the column subtitle.

   :return: the generated sub-header string

generateTitleHeader
^^^^^^^^^^^^^^^^^^^

.. java:method::  String generateTitleHeader()
   :outertype: TableColumn

   Generates the string that represents the header of the column, containing the column title.

   :return: the generated header string

getFormat
^^^^^^^^^

.. java:method::  String getFormat()
   :outertype: TableColumn

   :return: The format to be used to display the content of the column, according to the \ :java:ref:`String.format(java.lang.String,java.lang.Object...)`\  (optional).

getSubTitle
^^^^^^^^^^^

.. java:method::  String getSubTitle()
   :outertype: TableColumn

   :return: The subtitle to be displayed below the title of the column (optional).

getTable
^^^^^^^^

.. java:method::  Table getTable()
   :outertype: TableColumn

   :return: The table that the column belongs to.

getTitle
^^^^^^^^

.. java:method::  String getTitle()
   :outertype: TableColumn

   :return: The title to be displayed at the top of the column.

setFormat
^^^^^^^^^

.. java:method::  TableColumn setFormat(String format)
   :outertype: TableColumn

setSubTitle
^^^^^^^^^^^

.. java:method::  TableColumn setSubTitle(String subTitle)
   :outertype: TableColumn

setTable
^^^^^^^^

.. java:method::  TableColumn setTable(Table table)
   :outertype: TableColumn

setTitle
^^^^^^^^

.. java:method::  TableColumn setTitle(String title)
   :outertype: TableColumn

