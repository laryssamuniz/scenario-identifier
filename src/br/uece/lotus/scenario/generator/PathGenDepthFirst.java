package br.uece.lotus.scenario.generator;

import br.uece.lotus.Transition;
import br.uece.lotus.scenario.struct.LtsInfo;
import br.uece.lotus.scenario.struct.TestBundle;
import br.uece.lotus.scenario.struct.PathSet;
import br.uece.lotus.scenario.struct.PathStruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

abstract public class PathGenDepthFirst extends PathGenerator{

    @Override
    public PathSet generate(LtsInfo lts, TestBundle bundle){
        
        PathSet pathSet = new PathSet();
        Stack<PathStruct> stack = new Stack<>();

        // Initialization
        stack.addAll(initialize(bundle));

        while (!stack.isEmpty()){
            
            if (verifyAborted()){
                break;
            }

            PathStruct topPath = stack.pop();

            if (isPathCompleted(lts, topPath, bundle.getAdditionalFinalTransitionList())){
                
                pathSet.addPath(topPath);

                if (isGenerationCompleted(lts, pathSet)){
                    break;
                }
            }
            // Expand topPath
            stack.addAll(expand(topPath));
        }

        return pathSet;
    }

    /**
     * This method shall verify based on LtsInfo and the PathSet passed to it,
     * if the process (of path generation) should continue or not.
     *
     * @param lts
     * @param pathSet
     * @return -> true, if the process of generations of paths is completed
     * false, case not
     */
    abstract protected boolean isGenerationCompleted(LtsInfo lts, PathSet pathSet);

    protected List<PathStruct> initialize(TestBundle bundle){
        
        List<PathStruct> list = new ArrayList<>();

        for (Transition t : bundle.getInitialTransitionList()){
            
            PathStruct ps = new PathStruct();
            ps.addTransition(t);

            list.add(ps);
        }

        return list;
    }

    /**
     * This method shall perform the expansion of the path passed, adding new
     * potential paths in the stack.
     *
     * @param path
     * @return
     */
    abstract protected List<PathStruct> expand(PathStruct path);

    /**
     *
     * @param lts
     * @param ps
     * @param additionalFinalTransitions
     * @return -> true, if the path can be considered a terminated path, it ends
     * in a halt state / transition or has reached an infinite cycle -> false,
     * case not
     */
    abstract protected boolean isPathCompleted(LtsInfo lts, PathStruct ps, List<Transition> additionalFinalTransitions);
}
