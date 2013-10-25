package net.sf.javagimmicks.math.expression.parse.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.javagimmicks.math.expression.Expression;
import net.sf.javagimmicks.math.expression.parse.Parser;
import net.sf.javagimmicks.math.expression.parse.ParserPlugin;
import net.sf.javagimmicks.math.expression.parse.plugin.AddSubtractParserPlugin;
import net.sf.javagimmicks.math.expression.parse.plugin.BracketParserPlugin;
import net.sf.javagimmicks.math.expression.parse.plugin.LiteralParserPlugin;
import net.sf.javagimmicks.math.expression.parse.plugin.MultiplyDivideModParserPlugin;

public class DefaultParser implements Parser
{
    private final SortedSet<ParserPlugin> m_oPlugins = new TreeSet<>(new ParserPluginComparator());
    
    public DefaultParser(boolean bUseDefaultPlugins)
    {
        if(bUseDefaultPlugins)
        {
            addDefaultPlugins();
        }
    }
    
    public DefaultParser()
    {
        this(true);
    }
    
    public void addDefaultPlugins()
    {
        addPlugin(new AddSubtractParserPlugin());
        addPlugin(new MultiplyDivideModParserPlugin());
        addPlugin(new BracketParserPlugin());
        addPlugin(new LiteralParserPlugin());
    }

    @Override
    public void addPlugin(ParserPlugin oPlugin)
    {
        m_oPlugins.add(oPlugin);
    }

    @Override
    public Expression parse(String sExpressionString)
    {
        sExpressionString = sExpressionString.replaceAll("\\s", "");
        
        final DefaultContext oContext = new DefaultContext(sExpressionString);
        
        return oContext.parseSubexpression(0, sExpressionString.length());
    }

    private class DefaultContext extends AbstractParseContext
    {
        public DefaultContext(String sExpression)
        {
            super(sExpression);
        }

        protected DefaultContext(String sExpression, DefaultContext oParent)
        {
            super(sExpression, oParent);
        }

        @Override
        public SortedSet<ParserPlugin> getPlugins()
        {
            return Collections.unmodifiableSortedSet(m_oPlugins);
        }

        @Override
        protected AbstractParseContext createChildContext(String sChildExpression)
        {
            return new DefaultContext(sChildExpression, this);
        }
    }
    
    private static class ParserPluginComparator implements Comparator<ParserPlugin>
    {
        @Override
        public int compare(ParserPlugin o1, ParserPlugin o2)
        {
            return o2.getPriority() - o1.getPriority();
        }
    }
}