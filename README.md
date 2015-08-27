Scenario Identifier - SI
========================

1. About SI
-----------
Scenario Identifier is a plugin that lists the possible scenarios in the existing model and exports each setting for a LTS. To list the model of the scenarios we use the All One-Loop Paths* algorithm.

*All One-Loop Paths: this technique uses a depth-first search (DFS) to find all LTS paths that do not contain cycle and containing at most one cycle, each path generated will not contain any state repeated.

2. Using SI
-----------
The SI tool (Scenario Identifier) is a plugin for LoTuS modeling tool. LoTuS is a open-source tool to graphic behaviour modelling of software using LTS.

Step 1: Download the lotus tool - http://jeri.larces.uece.br/lotus ;

Step 2: Add the Scenario Identifier plugin [scenario-identifier.jar] in the Extensions folder that is located in the root directory of the LoTuS tool;

Step 3: Just launch the Lotus application (running java -jar lotus.jar) and Scenario Identifier plugin will automatically load ;

*LoTuS Github: https://github.com/lotus-tool

3. Interface SI
---------------
The plugin appears visually in the tool as the item "Scenario Identifier" from the main menu has a sub-menu: Export Scenarios.

When you click Export Scenarios, open a new tab in the Lotus tool containing the title with the project name - [ScenarioIdentifier] by clicking the tab, you can view the formal model and the list of scenarios generated from the All One-Loop algorithm Paths.
