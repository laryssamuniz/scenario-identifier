package br.uece.lotus.scenario.generator;

import br.uece.lotus.State;
import br.uece.lotus.Transition;
import br.uece.lotus.scenario.struct.LtsInfo;
import br.uece.lotus.scenario.struct.PathSet;
import br.uece.lotus.scenario.struct.PathStruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathGenAllFreeLoop extends PathGenDepthFirst{

    public PathGenAllFreeLoop(){}

    @Override
    public String getName(){
        return "All Free Loop Paths";
    }

    @Override
    public String getDescription(){
        return "This generator will return all paths in the LTS that do not contain any cycle, "
                + "consequently, each path generated will not contain any repeated state";
    }

    @Override
    protected List<PathStruct> expand(PathStruct path){
        
        List<PathStruct> list = new ArrayList<>();

        for (Transition t : path.getCurrentState().getOutgoingTransitions()){
            
            State lastState = t.getDestiny();

            if (!path.getVisitedStates().contains(lastState)){
                
                PathStruct newPath = new PathStruct(path);
                newPath.addTransition(t);

                list.add(newPath);
            }
        }
        return list;
    }

    @Override
    public boolean isPathCompleted(LtsInfo lts, PathStruct ps, List<Transition> additionalFinalTransitions){
        
        State current = ps.getCurrentState();

        if (current.isFinal() || current.isError()){
            return true;
        }

        if (additionalFinalTransitions != null){
            if (additionalFinalTransitions.contains(ps.getLastTransition())){
                return true;
            }
        }

        if (lts.getSelfLoopStateSet().contains(current)){
            return true;
        }

        // The getStateRange will only contain the state passed as parameter if there's a cycle returning to it
        Set<State> stateRange = lts.getStateRange(ps.getInitialState());

        int visitedStateSize = ps.getVisitedStates().size();

        // Verify cases that all the states were visited
        if (stateRange.size() - visitedStateSize <= 1){
            
            if (ps.getVisitedStates().size() == stateRange.size() + 1){
                return true;
            } else if (ps.getVisitedStates().size() == stateRange.size()
                    && stateRange.contains(ps.getInitialState())){
                return true;
            }
        }

        // Verify if there's some state to visit
        for (State s : lts.getAdjacencyStates(current)){
            if (!ps.getVisitedStates().contains(s)){
                return false;
            }
        }

        return false;
    }

    @Override
    protected boolean isGenerationCompleted(LtsInfo lts, PathSet pathSet){
        return false;
    }

    @Override
    public boolean acceptParameter(){
        return false;
    }

    @Override
    public String getParameterText(){
        return null;
    }

    @Override
    public void setParameter(String value){
    }

    @Override
    public boolean acceptPurpose(){
        return true;
    }

    @Override
    public boolean acceptSelector(){
        return true;
    }
}
