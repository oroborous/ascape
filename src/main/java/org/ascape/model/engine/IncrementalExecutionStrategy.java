/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.util.ResetableIterator;

/*
 * User: Miles Parker
 * Date: Sep 25, 2003
 * Time: 4:57:48 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Class IncrementalExecutionStrategy.
 */
public abstract class IncrementalExecutionStrategy extends ExecutionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The factory.
     */
    StrategyFactory factory;

    /**
     * The agent iterator.
     */
    private ResetableIterator agentIterator;
    
    /**
     * The agent selector.
     */
    AgentSelector agentSelector;
    
    /**
     * The rule selector.
     */
    RuleSelector ruleSelector;
    
    /**
     * The current rule.
     */
    Rule currentRule;
    
    /**
     * The current agent.
     */
    Agent currentAgent;

    /**
     * Instantiates a new incremental execution strategy.
     * 
     * @param factory
     *            the factory
     */
    public IncrementalExecutionStrategy(StrategyFactory factory) {
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#execute()
     */
    public void execute() {
        reset();
        while (hasNext()) {
            increment();
            fire();
        }
    }

    /**
     * Fire.
     */
    public void fire() {
        currentRule.execute(currentAgent);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#reset()
     */
    public void reset() {
        getAgentSelector().reset();
        getRuleSelector().reset();
    }

    /**
     * Gets the current agent.
     * 
     * @return the current agent
     */
    public Agent getCurrentAgent() {
        return currentAgent;
    }

    /**
     * Gets the current rule.
     * 
     * @return the current rule
     */
    public Rule getCurrentRule() {
        return currentRule;
    }

    /**
     * Checks for next.
     * 
     * @return true, if successful
     */
    public abstract boolean hasNext();

    /**
     * Increment.
     */
    public abstract void increment();

    /**
     * Gets the agent iterator.
     * 
     * @return the agent iterator
     */
    public ResetableIterator getAgentIterator() {
        return agentIterator;
    }

    /**
     * Sets the agent iterator.
     * 
     * @param agentIterator
     *            the new agent iterator
     */
    public void setAgentIterator(ResetableIterator agentIterator) {
        this.agentIterator = agentIterator;
    }

    /**
     * Gets the rules.
     * 
     * @return the rules
     */
    public final Object[] getRules() {
        return factory.getRules();
    }

    /**
     * Gets the agent selector.
     * 
     * @return the agent selector
     */
    public AgentSelector getAgentSelector() {
        return agentSelector;
    }

    /**
     * Gets the rule selector.
     * 
     * @return the rule selector
     */
    public RuleSelector getRuleSelector() {
        return ruleSelector;
    }

    /**
     * Sets the agent selector.
     * 
     * @param agentSelector
     *            the new agent selector
     */
    public void setAgentSelector(AgentSelector agentSelector) {
        this.agentSelector = agentSelector;
    }

    /**
     * Sets the rule selector.
     * 
     * @param ruleSelector
     *            the new rule selector
     */
    public void setRuleSelector(RuleSelector ruleSelector) {
        this.ruleSelector = ruleSelector;
    }

    /**
     * Gets the scape.
     * 
     * @return the scape
     */
    public final Scape getScape() {
        return factory.getScape();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#clone()
     */
    public Object clone() {
        IncrementalExecutionStrategy clone = (IncrementalExecutionStrategy) super.clone();
        clone.agentIterator = null;
        if (clone.agentSelector != null) {
            clone.agentSelector = (AgentSelector) this.agentSelector.clone();
            clone.agentSelector.setStrategy(clone);
        } else {
            clone.agentSelector = null;
        }
        if (clone.ruleSelector != null) {
            clone.ruleSelector = (RuleSelector) this.ruleSelector.clone();
        } else {
            clone.ruleSelector = null;
        }
        return clone;
    }
}
