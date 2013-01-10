package de.consistec.syncframework.android.adapter;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.data.schema.Constraint;
import de.consistec.syncframework.common.data.schema.ConstraintType;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.Table;
import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database adapter for SQLite on Ice Cream Sandwich platform.
 * <ul>
 * <li><b>Company:</b>&nbsp;consistec Engineering and Consulting GmbH</li>
 * <li><b>Date:</b>&nbsp;03.08.12</li>
 * <li><b>Time:</b>&nbsp;13:24</li>
 * </ul>
 * <p/>
 * @author Markus Backes
 */
public class ICSSQLiteDatabaseAdapter extends GenericDatabaseAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ICSSQLiteDatabaseAdapter.class);
    private static final Config CONF = Config.getInstance();
    private String catalog = null;

    @Override
    public boolean hasSchema() throws DatabaseAdapterException {
        if (this.getTableNamesFromDatabase().containsAll(CONF.getSyncTables())) {
            // check if all configured tables exist
            createMDSchema(false);
            return true;
        }
        return false;
    }

    @Override
    public void applySchema(Schema s) throws DatabaseAdapterException {

        Statement stmt = null; //NOSONAR

        try {

            removeExistentTablesFromSchema(s);
            String sqlSchema = getSchemaConverter().toSQL(s);

            LOG.debug("applying schema: {}", sqlSchema);

            String[] tableScripts = sqlSchema.split(";");

            for (String tableSql : tableScripts) {
                stmt = connection.createStatement();
                stmt.executeUpdate(tableSql);
                stmt.close();
            }

        } catch (SQLException e) {
            throw new DatabaseAdapterException("could not apply schema", e);
        } catch (SchemaConverterException e) {
            throw new DatabaseAdapterException("could not convert schema to sql script", e);
        } finally {
            closeStatements(stmt);
        }
    }

    private void removeExistentTablesFromSchema(Schema s) throws DatabaseAdapterException {
        List<String> databaseTables = getTableNamesFromDatabase();
        for (Iterator<Table> itr = s.getTables().iterator(); itr.hasNext();) {
            Table table = itr.next();
            if (databaseTables.contains(table.getName())) {
                itr.remove();
            }
        }
    }

    @Override
    protected List<String> getTableNamesFromDatabase() throws DatabaseAdapterException {
        ResultSet tables = null; //NOSONAR
        List<String> databaseTables = newArrayList();
        try {
            tables = connection.getMetaData().getTables("", "", "%", new String[]{"table"});
            while (tables.next()) {
                // create a list with all table names from database
                databaseTables.add(tables.getString(TABLE_NAME));
            }
        } catch (SQLException ex) {
            throw new DatabaseAdapterException("could not enumerate tables from database", ex);
        } finally {
            closeResultSets(tables);
        }
        return databaseTables;
    }

    @Override
    public void createMDSchema(boolean isServerSchema) throws DatabaseAdapterException {
        List<String> databaseTables = getTableNamesFromDatabase();
        Schema schema = new Schema();
        Table table;
        for (String tableName : CONF.getSyncTables()) {
            String mdTableName = tableName + CONF.getMdTableSuffix();

            LOG.debug("searching md table for table with name: {}", tableName);

            if (databaseTables.contains(tableName) && databaseTables.contains(mdTableName)) {

                LOG.debug("skipping creation of table: {}. Does already exist.", tableName);
                // skip table creation because table already exists
                continue;
            }

            LOG.debug("creating new table: {}", mdTableName);

            table = new Table(mdTableName);
            Column pkColumn = getPrimaryKeyColumn(tableName);
            table.add(new Column("pk", pkColumn.getType(), pkColumn.getSize(), pkColumn.getDecimalDigits(), false));
            table.add(new Column("mdv", Types.VARCHAR, 500, 0, true));
            table.add(new Column("rev", Types.INTEGER, 0, 0, true));

            if (!isServerSchema) {
                table.add(new Column("f", Types.INTEGER, 0, 0, true));
            }

            table.add(new Constraint(ConstraintType.PRIMARY_KEY, "MDPK", "pk"));
            schema.addTables(table);
        }
        if (schema.getTables().size() > 0) {
            applySchema(schema);
        }
    }

    @Override
    public Column getPrimaryKeyColumn(String table) throws DatabaseAdapterException {

        LOG.debug("searching primary key column for table {}", table);
        ResultSet primaryKeys = null; //NOSONAR
        ResultSet columns = null; //NOSONAR
        String primaryKeyColumnName;

        try {

            primaryKeys = connection.getMetaData().getPrimaryKeys(null, null, table);

            if (primaryKeys.next()) {

                primaryKeyColumnName = primaryKeys.getString(COLUMN_NAME);
                LOG.debug("found primary key column: {}", primaryKeyColumnName);
                columns = connection.getMetaData().getColumns(catalog, getSchemaOfConnection(), table, null);

                while (columns.next()) {

                    if (primaryKeyColumnName.equalsIgnoreCase(columns.getString(COLUMN_NAME))) {
                        return new Column(primaryKeyColumnName, columns.getInt(DATA_TYPE), columns.getInt(COLUMN_SIZE),
                            columns.getInt(DECIMAL_DIGITS), columns.getBoolean(NULLABLE));
                    }

                }
            }
            throw new DatabaseAdapterException("No primary key found");
        } catch (SQLException e) {
            throw new DatabaseAdapterException("an error occurred during reading the primary key column name", e);
        } finally {
            closeResultSets(primaryKeys, columns);
        }
    }

    @Override
    public List<String> getColumns(String tableName) throws DatabaseAdapterException {

        LOG.debug("Reading columns for table {}", tableName);
        ResultSet columns = null; //NOSONAR
        List<String> columnList;

        try {
            columns = connection.getMetaData().getColumns(catalog, getSchemaOfConnection(), tableName, null);
            columnList = newArrayList();

            while (columns.next()) {
                columnList.add(columns.getString(COLUMN_NAME));
            }

        } catch (SQLException e) {
            throw new DatabaseAdapterException("could not read columns for table: " + tableName, e);
        } finally {
            closeResultSets(columns);
        }
        return columnList;
    }

    @Override
    public Schema getSchema() throws DatabaseAdapterException {
        try {

            DatabaseMetaData metaData = connection.getMetaData();
            Schema schema = new Schema();
            ResultSet columns = null; //NOSONAR
            ResultSet primaryKeys = null; //NOSONAR
            Table table;
            Column column;
            Constraint constraint;

            try {

                for (String tableName : CONF.getSyncTables()) {

                    table = new Table(tableName);
                    columns = metaData.getColumns(catalog, getSchemaOfConnection(), tableName, null);

                    while (columns.next()) {

                        column = new Column(columns.getString(COLUMN_NAME), columns.getInt(DATA_TYPE));

                        if ("0".equalsIgnoreCase(columns.getString(NULLABLE))) {
                            column.setNullable(false);
                        }

                        column.setSize(columns.getInt(COLUMN_SIZE));
                        column.setDecimalDigits(columns.getInt(DECIMAL_DIGITS));
                        table.add(column);
                    }

                    primaryKeys = metaData.getPrimaryKeys(catalog, getSchemaOfConnection(), tableName);

                    while (primaryKeys.next()) {

                        constraint = new Constraint(ConstraintType.PRIMARY_KEY, primaryKeys.getString(PK_NAME),
                            primaryKeys.getString(COLUMN_NAME));
                        table.add(constraint);
                    }

                    schema.addTables(table);
                }
            } finally {
                closeResultSets(columns, primaryKeys);
            }
            return schema;
        } catch (SQLException e) {
            throw new DatabaseAdapterException("an error occurred while creating sql schema", e);
        }
    }
}
