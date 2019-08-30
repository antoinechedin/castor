# UML Models

Models were made with [StarUML](http://staruml.io/) free version.

### Olap4j

![olap4j model][olap4j]

#### olap4j.metatda package:

This is how olap4j represents the OLAP structure. We have the classic Schema -> 
Dimension -> Hierarchy -> Level -> Member.

It's interesting to see that Measure is a subclass of Member, meaning that Measure have a special Dimension, Hierarchy and so on.
Also, measure and member can be differentiated from each other by the type attribute.

#### olap4j package:

When executing a MDX query with olap4j, you will get a CellSet as an answer. From here you can get cells by its coordinates in
the table. Since MDX allow multi-dimensional request, the coordinates are represented by a list of integer.

In order to link the CellSet with the cube schema, you need to use the Position class. A Position represent a table header cell.

#### castor:

Since the olap4j.CellSet usage may be complicated in specific use case. Unfortunately, olap4j.CellSet cannot be extended, so instead of implementing a sub-class, I had to write a wrapper with an association to the original cellSet.
The castor.CellSet use matrix to store cell data (like olap4.CellSet) and a Tree structure to modelize the table headers.
HeaderTree class represent a node, and have may have multiple HeaderTree child node. The span attribute is equals to the number of child th node have.

#### propositon:

castor package is usable but isn't perfect and present many flaws in its structure. The proposition package isn't currently implemented, and want to provide a better model.
The main part is the qualified association between the uery class and Level. It allows to compare Queries together 

[olap4j]: https://github.com/antoinechedin/castor/blob/master/intentional-analytics-model/uml/olap4j.jpg "Olap4j UML model"
