package br.uece.lotus.scenario.utils;

import br.uece.lotus.State;
import br.uece.lotus.Transition;
import java.util.HashSet;
import java.util.Set;

public class StateUtils {

    public static boolean isSelfLoopState(State s){
        
        for (Transition transition : s.getOutgoingTransitions()){
            if (transition.getDestiny() != s){
                return false;
            }
        }

        return true;
    }

    public static Set<Transition> getOutgoingTransitions(State s){
        
        Set<Transition> transitions = new HashSet<>();

        for (Transition t : s.getOutgoingTransitions()){
            transitions.add(t);
        }

        return transitions;
    }
}
