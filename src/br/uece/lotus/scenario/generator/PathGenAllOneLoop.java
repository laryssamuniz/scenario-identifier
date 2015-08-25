package br.uece.lotus.scenario.generator;

import br.uece.lotus.State;
import br.uece.lotus.Transition;
import br.uece.lotus.scenario.struct.PathStruct;
import java.util.ArrayList;
import java.util.List;

public class PathGenAllOneLoop extends PathGenAllFreeLoop{

    @Override
    public String getName(){
        return "All One Loop Paths";
    }

    @Override
    public String getDescription(){
        return "This generator will return all paths in the LTS that contain at most one cycle, "
                + "consequently, each path generated will contain at most one and only one repeated state";
    }

    @Override
    protected List<PathStruct> expand(PathStruct path){
        
        List<PathStruct> list = new ArrayList<>();

        for (Transition t : path.getCurrentState().getOutgoingTransitions()){
            
            State lastState = t.getDestiny();

            if (!path.getVisitedStates().contains(lastState) || path.getRepeatedStatesCount() == 0){
                
                PathStruct newPath = new PathStruct(path);
                newPath.addTransition(t);

                list.add(newPath);
            }
        }
        return list;
    }
}
