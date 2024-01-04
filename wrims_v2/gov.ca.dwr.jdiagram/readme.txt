JDiagram control, version 3.2


***** ABOUT JDIAGRAM *****

 JDiagram is a Java Swing component that provides Swing based applications with the ability to create and display various kinds of diagrams. The package also includes an applet class that can be loaded in web pages. The component supports workflow, flowchart and process diagrams, object hierarchy and relationship charts, entity-relationship diagrams, structures like graphs and trees, etc.
 Every behavioral and appearance aspect of JDiagram can be customized as suits best your project. Diagram objects can have different styles, colors, fonts and pictures.
 The control's programming model comprises more than 300 methods, properties and events.


***** SOURCE CODE *****

 A JDiagram copy can be purchased with the control's full source code. JDiagram is developed entirely in Java using the Swing API. 
 If you choose to buy source code-packed version of the control, please provide your phone number in the appropriate field of the online order form. Due to many frauds that we have experienced in the past, the source code is no longer available for immediate download after the purchase. It will be emailed to you after MindFusion's representative contacts you successfully via the phone number provided.
 We may decide to skip the phone check and email the source code  directly in case the control is ordered by a company and the given billing email corresponds to the company's web domain.


***** SAMPLES *****

~ Anchors ~ demonstrates how to define and use anchor point patterns.

~ AnnealLayout ~ demonstrates the AnnealLayout automatic layout algorithm.

~ CustomDraw ~ uses custom painting of links to show animated progress of a process running in the system represented by a diagram.

~ DirTree ~ uses custom-designed shapes to represent folder icons. It also demonstrates how to use JDiagram as a tree control, utilizing automatic tree arrangement and collapsing and expanding of sub-trees.

~ Fishbone ~ is a fishbone diagram editor demonstrating how to implement custom drawing, hit-testing and interaction handling in derived node types.

~ FlowCharter ~ a small application for drawing diagrams and flowcharts. It lets end-users drag shapes from a palette to construct diagram nodes. The sample also demonstrates the available arrowhead shapes, anchor points usage, saving and loading files and printing.

~ IconNodes ~ shows how to create a custom node type that displays an image and a label below the image.

~ InteractiveTable ~ shows how to present a combo-list of options for each row of a table. That is not a  built-in feature of JDiagram but can be easily implemented by showing a combo when a row is clicked.

~ JDiagDemo ~ demonstrates most of the features of JDiagram.

~ JspSample ~ shows two possible ways to use JDiagram in a web application.

~ LayeredLayout ~ demonstrates the LayeredLayout graph layout algorithm.

~ MultiViews ~ shows how to display the same Diagram in multiple views.

~ PredShapes ~ shows the predefined JDiagram shapes and their identifiers.

~ Routing ~ demonstrates the automatic link routing algorithm.

~ SiteMap ~ shows how to attach custom data to diagram nodes. That data is saved/loaded together with the JDiagram document. Also demonstrates how to iterate over the diagram items to create a sample site-map.

~ SWDesigner ~ uses JDiagram to create and edit simple UML-like class diagrams interactively.

~ XmlTree ~ demonstrates how to build a tree from arbitrary XML data.


***** HISTORY *****

new in version 3.2:
  support for background swimlane grid
  Swimlane diagram layout algorithm
  Cascading graph layout algorithm
  Triangular graph layout algorithm
  Hierarchical graph layout algorithm
  Ortogonal graph layout improvements

new in version 3.1:
  topological graph layout algorithm
  shadows can be painted using any brush type
  support for custom arrowheads
  Granularity property added to QuickRouter
  ShowToolTips property added to DiagramView

new in version 3.0:
  the TreeViewNode class represents nodes that can display hierarchical data
  Ruler control
  export diagrams to SVG
  Link routing enhancements
  Flowchart graph layout
  LayeredLayout can straighten links that cross multiple layers in the graph
  Frozen nodes in SpringLayout
  Organizational charts with assistant nodes as in MS Office
  improved speed of GridLayout and OrthogonalLayout
  Adjustment handles styling
  new ScriptHelper methods
  DiagramApplet provides local clipboard support
  Shape elements can be assigned distinct color and line styles
  ShapeListBox.ShowLabels specifies whether to display item labels
  links custom drawing
  PdfExporter creates hyperlinks for nodes whose HyperLink property is set
  drawForeground event lets your render custom graphics in the diagram foreground

new in version 2.2:
  PdfExporter lets you export JDiagram documents to PDF

new in version 2.1:
  Orthogonal graph layout algorithm
  Circular graph layout algorithm
  mergeUndoRecords() lets you merge multiple undo records into a composite one
  setExpandButtonPosition() sets the position of collapse/expand icons

new in version 2.0:
  model / view architecture
  clipboard support
  container nodes
  laying out unconnected subgraphs so that the diagram covers a minimal area
  Orthogonal graph layout
  the text of links can be edited in-place 

new in version 1.2.2:
  wrapping text to new lines can be disabled, clipping the text if it overflows
  now text can wrap to a new line at arbitrary position in the text
  ScriptHelper.loadShapeLibrary lets you load a custom shape library from JavaScript
  ScriptHelper.createTextFormat lets you create TextFormat objects from JavaScript
  FlowChartApplet.createFlowChart can be overridden to return an object derived from FlowChart
  the OverviewApplet class implement an applet that wraps the Overview control
  BehaviorType.Pan enables panning without using the Alt key
  the ENTER and ESC keys can be used to accept or cancel inplace editing
  now the itemModified event lets you find out the selection handle used to modify an item
  the nodesIntersect method lets you find out if two nodes are overlapping
  the cancelDrag method of ValidationEvent lets you immediately cancel the operation

new in version 1.2.1:
  now AnnealLayout takes into consideration the size of nodes
  the Overview tracking rectangle borders width does not depend on the zoom level
  the tracking rectangle follows the mouse more precisely
  setTrackingRectPen lets you specify the color and width of the tracking rectangle borders
  the itemSelecting validation event is called when an user tries to select an item
  mouseEnterItem / mouseLeaveItem events raised when the mouse enters / leaves an item
  a custom shape definition can have a text area larger than the box bounds
  some drawing glitches have been fixed
  text smoothing mode is set separately from the drawing primitives’ one using setTextAntiAlias
  setHitTestPriority lets you change the order in which items are hit-tested
  the shape of items is considered when selecting them with a selection rectangle
  resizeToFitText makes a box big enough to display its text without clipping

new in version 1.2:
  an Overview control that provides a scaled-down view of the diagram
  a ShapeListBox control that displays shapes icons and allows drag-and-drop creation of boxes
  support for Shape Library XML files that contain custom shape definitions
  AnnealLayout layout class
  the setImageUrl method lets you load a background image from URL
  the getNodesAt method lets you find all nodes lying at the specified location.
  the setRotateText method lets you enable or disable text rotation in boxes
  the resizeToFitText resizes a table’s rows and columns so that the table cells fit their text

new in version 1.1:
  now SaveToString and LoadFromString use base64 encoding 
  print preview window
  print scaling
  XML serialization into/from String objects
  itemDeleting event added
  the FlowChartApplet class lets you use JDiagram as an applet
  MeasureUnit property added

version 1.0:
  initial release


***** CONTACT US *****

for any questions you might have about using the control:
support@mindfusion.eu
+359877652251

for administrative and sales information:
sales@mindfusion.eu

our web site:
http://mindfusion.eu


***** COPYRIGHTS *****

Copyright (C) 2005-2011, MindFusion LLC - Bulgaria
All rights reserved.