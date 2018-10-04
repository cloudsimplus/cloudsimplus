.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.function Function

TableBuilderAbstract
====================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public abstract class TableBuilderAbstract<T>

   An abstract class to build tables to print data from a list of objects containing simulation results.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TableBuilderAbstract
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TableBuilderAbstract(List<? extends T> list)
   :outertype: TableBuilderAbstract

   Instantiates a builder to print the list of objects T using the a default \ :java:ref:`TextTable`\ . To use a different \ :java:ref:`Table`\ , check the alternative constructors.

   :param list: the list of objects T to print

TableBuilderAbstract
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TableBuilderAbstract(List<? extends T> list, Table table)
   :outertype: TableBuilderAbstract

   Instantiates a builder to print the list of objects T using the a given \ :java:ref:`Table`\ .

   :param list: the list of objects T to print
   :param table: the \ :java:ref:`Table`\  used to build the table with the object data

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: public TableBuilderAbstract<T> addColumn(TableColumn col, Function<T, Object> dataFunction)
   :outertype: TableBuilderAbstract

   Dynamically adds a column to the end of the table to be built.

   :param col: the column to add
   :param dataFunction: a function that receives a Cloudlet and returns the data to be printed for the added column

addColumn
^^^^^^^^^

.. java:method:: public TableBuilderAbstract<T> addColumn(int index, TableColumn col, Function<T, Object> dataFunction)
   :outertype: TableBuilderAbstract

   Dynamically adds a column to a specific position into the table to be built.

   :param index: the position to insert the column.
   :param col: the column to add
   :param dataFunction: a function that receives a Cloudlet and returns the data to be printed for the added column

addColumnDataFunction
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected TableBuilderAbstract<T> addColumnDataFunction(TableColumn col, Function<T, Object> function)
   :outertype: TableBuilderAbstract

addDataToRow
^^^^^^^^^^^^

.. java:method:: protected void addDataToRow(T object, List<Object> row)
   :outertype: TableBuilderAbstract

   Add data to a row of the table being generated.

   :param object: The object T to get to data to show in the row of the table
   :param row: The row that the data from the object T will be added to

build
^^^^^

.. java:method:: public void build()
   :outertype: TableBuilderAbstract

   Builds the table with the data from the list of objects and shows the results.

createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void createTableColumns()
   :outertype: TableBuilderAbstract

   Creates the columns of the table and define how the data for those columns will be got from an object inside the \ :java:ref:`list`\  of objects to be printed.

getTable
^^^^^^^^

.. java:method:: protected Table getTable()
   :outertype: TableBuilderAbstract

setObjectList
^^^^^^^^^^^^^

.. java:method:: protected final TableBuilderAbstract<T> setObjectList(List<? extends T> list)
   :outertype: TableBuilderAbstract

   Sets a List of objects T to be printed.

   :param list: List of objects T to set

setTable
^^^^^^^^

.. java:method:: protected final TableBuilderAbstract<T> setTable(Table table)
   :outertype: TableBuilderAbstract

   Sets the \ :java:ref:`Table`\  used to build the table with Cloudlet Data. The default table builder is \ :java:ref:`TextTable`\ .

   :param table: the \ :java:ref:`Table`\  to set

setTitle
^^^^^^^^

.. java:method:: public TableBuilderAbstract<T> setTitle(String title)
   :outertype: TableBuilderAbstract

