package jmri.jmrit.logixng.analog.expressions;

import java.text.NumberFormat;
import java.util.Locale;
import jmri.InstanceManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant value.
 * This can be useful for example by the ActionThrottle.
 * 
 * @author Daniel Bergqvist Copyright 2019
 */
public class AnalogExpressionConstant extends AbstractAnalogExpression {

    private double _value;
    
    public AnalogExpressionConstant(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        
        super(sys, user);
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.ITEM;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return false;
    }
    
    public void setValue(double value) {
        assertListenersAreNotRegistered(log, "setValue");
        _value = value;
    }
    
    public double getValue() {
        return _value;
    }
    
    /** {@inheritDoc} */
    @Override
    public double evaluate() {
        return _value;
    }
    
    /** {@inheritDoc} */
    @Override
    public void reset() {
        // Do nothing
    }
    
    @Override
    public FemaleSocket getChild(int index)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getShortDescription(Locale locale) {
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return Bundle.getMessage(locale, "AnalogExpressionConstant1", numberFormat.format(_value));
    }

    @Override
    public String getLongDescription(Locale locale) {
        return getShortDescription(locale);
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        // This class does not have any listeners registered, but we still don't
        // want a caller to change the value then listeners are registered.
        // So we set this property to warn the caller when the caller is using
        // the class in the wrong way.
        _listenersAreRegistered = true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        _listenersAreRegistered = false;
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(AnalogExpressionConstant.class);
    
}
