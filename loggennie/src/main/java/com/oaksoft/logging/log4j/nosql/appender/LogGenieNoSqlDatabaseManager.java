package com.oaksoft.logging.log4j.nosql.appender;

import java.io.Serializable;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.nosql.appender.NoSqlConnection;
import org.apache.logging.log4j.nosql.appender.NoSqlObject;
import org.apache.logging.log4j.nosql.appender.NoSqlProvider;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public final class LogGenieNoSqlDatabaseManager<W> extends AbstractDatabaseManager
{
    private static final LogGenieNoSqlDatabaseManagerFactory FACTORY = new LogGenieNoSqlDatabaseManagerFactory();

    private final NoSqlProvider<NoSqlConnection<W, ? extends NoSqlObject<W>>> provider;

    private NoSqlConnection<W, ? extends NoSqlObject<W>> connection;

    private Layout<? extends Serializable> layout;

    private LogGenieNoSqlDatabaseManager(final String name, final int bufferSize, final NoSqlProvider<NoSqlConnection<W, ? extends NoSqlObject<W>>> provider)
    {
        super(name, bufferSize);
        this.provider = provider;
    }

	@Override
	protected void startupInternal() throws Exception
	{
		 // nothing to see here
	}

	@Override
	protected void shutdownInternal() throws Exception
	{
		Closer.closeSilently(this.connection);
	}

    @Override
    protected void connectAndStart()
    {
        try
        {
            this.connection = this.provider.getConnection();
        }
        catch (final Exception e)
        {
            throw new AppenderLoggingException("Failed to get connection from NoSQL connection provider.", e);
        }
    }

	@Override
	protected void writeInternal(LogEvent event)
	{
        if (!this.isRunning() || this.connection == null || this.connection.isClosed())
        {
            throw new AppenderLoggingException("Cannot write logging event; NoSQL manager not connected to the database.");
        }

        if (getLayout() == null)
        {
            setLayout(PatternLayout.createDefaultLayout());
        }

    	String msg = getLayout().toSerializable(event).toString();

    	final NoSqlObject<W> entity = this.connection.createObject();

//    	JsonSlurper jsonSlurper = new JsonSlurper();

		//Map<String, Object> map = (Map<String, Object>) jsonSlurper.parseText(msg);

		Object jsonObject = JSON.parse(msg);
		DBObject dbObject = (DBObject) jsonObject;

		for (String entry: dbObject.keySet())
		{
			entity.set(entry, dbObject.get(entry));
		}

    	this.connection.insertObject(entity);
	}

    @Override
    protected void commitAndClose()
    {
        // all NoSQL drivers auto-commit (since NoSQL doesn't generally use the concept of transactions).
        // also, all our NoSQL drivers use internal connection pooling and provide clients, not connections.
        // thus, we should not be closing the client until shutdown as NoSQL is very different from SQL.
        // see LOG4J2-591 and LOG4J2-676
    }

    public static LogGenieNoSqlDatabaseManager<?> getNoSqlDatabaseManager(final String name, final int bufferSize, final NoSqlProvider<?> provider)
    {
    	return AbstractDatabaseManager.getManager(name, new FactoryData(bufferSize, provider), FACTORY);
    }

	/**
     * Encapsulates data that {@link NoSQLDatabaseManagerFactory} uses to create managers.
     */
    private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData
    {
        private final NoSqlProvider<?> provider;

        protected FactoryData(final int bufferSize, final NoSqlProvider<?> provider)
        {
            super(bufferSize);
            this.provider = provider;
        }
    }

    /**
     * Creates managers.
     */
    private static final class LogGenieNoSqlDatabaseManagerFactory implements ManagerFactory<LogGenieNoSqlDatabaseManager<?>, FactoryData>
    {
        @Override
        @SuppressWarnings({ "unchecked", "rawtypes"})
        public LogGenieNoSqlDatabaseManager<?> createManager(final String name, final FactoryData data)
        {
            return new LogGenieNoSqlDatabaseManager(name, data.getBufferSize(), data.provider);
        }
    }

	public Layout<? extends Serializable> getLayout()
	{
		return layout;
	}

	public void setLayout(Layout<? extends Serializable> layout)
	{
		this.layout = layout;
	}
}
