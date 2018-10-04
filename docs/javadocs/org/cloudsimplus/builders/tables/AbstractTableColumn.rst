.. java:import:: org.apache.commons.lang3 StringUtils

AbstractTableColumn
===================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public abstract class AbstractTableColumn implements TableColumn

   A column of a table to be generated using a \ :java:ref:`Table`\  class.

   :author: Manoel Campos da Silva Filho

Constructors
------------
AbstractTableColumn
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractTableColumn(Table table, String title)
   :outertype: AbstractTableColumn

   Creates a column with a specific title.

   :param table: The table that the column belongs to.
   :param title: The column title.

AbstractTableColumn
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractTableColumn(String title, String subTitle)
   :outertype: AbstractTableColumn

   Creates a column with a specific title and sub-title.

   :param title: The column title.
   :param subTitle: The column sub-title.

AbstractTableColumn
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractTableColumn(Table table, String title, String subTitle)
   :outertype: AbstractTableColumn

   Creates a column with a specific title and sub-title for a given table.

   :param title: The column title.
   :param subTitle: The column sub-title.

Methods
-------
generateData
^^^^^^^^^^^^

.. java:method:: @Override public String generateData(Object data)
   :outertype: AbstractTableColumn

   Generates the string that represents the data of the column, formatted according to the \ :java:ref:`format <getFormat()>`\ .

   :param data: The data of the column to be formatted
   :return: a string containing the formatted column data

generateHeader
^^^^^^^^^^^^^^

.. java:method:: protected abstract String generateHeader(String str)
   :outertype: AbstractTableColumn

   Generates a header for the column, either for the title or subtitle header.

   :param str: header title or subtitle
   :return: the generated header string

generateSubtitleHeader
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public String generateSubtitleHeader()
   :outertype: AbstractTableColumn

generateTitleHeader
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public String generateTitleHeader()
   :outertype: AbstractTableColumn

getFormat
^^^^^^^^^

.. java:method:: @Override public String getFormat()
   :outertype: AbstractTableColumn

   :return: The format to be used to display the content of the column, according to the \ :java:ref:`String.format(java.lang.String,java.lang.Object...)`\  (optional).

getIndex
^^^^^^^^

.. java:method:: protected int getIndex()
   :outertype: AbstractTableColumn

   :return: The index of the current column into the column list of the \ :java:ref:`Table <getTable()>`\ .

getSubTitle
^^^^^^^^^^^

.. java:method:: @Override public String getSubTitle()
   :outertype: AbstractTableColumn

   :return: The subtitle to be displayed below the title of the column (optional).

getTable
^^^^^^^^

.. java:method:: @Override public Table getTable()
   :outertype: AbstractTableColumn

   :return: The table that the column belongs to.

getTitle
^^^^^^^^

.. java:method:: @Override public String getTitle()
   :outertype: AbstractTableColumn

   :return: The title to be displayed at the top of the column.

isLastColumn
^^^^^^^^^^^^

.. java:method:: protected boolean isLastColumn()
   :outertype: AbstractTableColumn

   Indicates if the current column is the last one in the column list of the \ :java:ref:`Table <getTable()>`\ .

   :return: true if it is the last column, false otherwise.

setFormat
^^^^^^^^^

.. java:method:: @Override public final AbstractTableColumn setFormat(String format)
   :outertype: AbstractTableColumn

setSubTitle
^^^^^^^^^^^

.. java:method:: @Override public AbstractTableColumn setSubTitle(String subTitle)
   :outertype: AbstractTableColumn

setTable
^^^^^^^^

.. java:method:: @Override public AbstractTableColumn setTable(Table table)
   :outertype: AbstractTableColumn

setTitle
^^^^^^^^

.. java:method:: @Override public AbstractTableColumn setTitle(String title)
   :outertype: AbstractTableColumn

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: AbstractTableColumn

