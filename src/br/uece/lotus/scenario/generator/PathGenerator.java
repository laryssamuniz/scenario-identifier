package br.uece.lotus.scenario.generator;

import br.uece.lotus.scenario.struct.LtsInfo;
import br.uece.lotus.scenario.struct.PathSet;
import br.uece.lotus.scenario.struct.TestBundle;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract public class PathGenerator{

    public abstract String getName();

    public abstract String getDescription();
    
    protected boolean mStopGeneration = false;
    
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public final void abortGeneration(){
        
        lock.writeLock().lock();

        try{
            mStopGeneration = true;
        } finally{
            lock.writeLock().unlock();
        }
    }

    protected final boolean verifyAborted(){
        
        boolean ret;

        lock.readLock().lock();
        try{
            ret = mStopGeneration;
        } finally{
            lock.readLock().unlock();
        }

        return ret;
    }

    abstract public boolean acceptParameter();

    /**
     *
     * @return -> The text string that will be shown in user interface, in case
     * the acceptParameter returns true
     */
    abstract public String getParameterText();

    abstract public void setParameter(String value);

    abstract public boolean acceptSelector();

    abstract public boolean acceptPurpose();

    public final PathSet startGeneration(LtsInfo lts, TestBundle bundle){
        mStopGeneration = false;
        return generate(lts, bundle);
    }

    abstract protected PathSet generate(LtsInfo lts, TestBundle bundle);

    @Override
    public String toString(){
        return getName();
    }
}
