package DAO;
// import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import annotations.BaseObject;
import annotations.Column;
import annotations.PrimaryKey;

public  class DB {

    public String toString(String separateur) throws Exception{
        StringBuilder builder= new StringBuilder();
        Field[] fields=this.getClass().getDeclaredFields();
        for (Field field : fields) {
                field.setAccessible(true);
            if (field.isAnnotationPresent(BaseObject.class)) {
                Object value=field.getType().getDeclaredMethod("getId").invoke(field.get(this));
                builder.append(value).append(separateur);                             
            }
            
            else {
                builder.append(field.get(this)).append(separateur);    
            }
            
        }
        return builder.substring(0, builder.length() - 1);
    }

    public boolean checkIfDateorString(Field field)
        {
            if (field.getType()==LocalDate.class) {
                return true;

            }
            else if(field.getType()==java.sql.Date.class){
                return true;
            }
            else if(field.getType()==Timestamp.class)
            {
                return true;
            }
            else if (field.getType()==String.class) {
                return true;
            }
            return false;
        }
    public List<DB> rechercheMultiCritaire(Connection conn,String... critaires) throws Exception
        {
                return recherche(this.makeAfterWhere(critaires),conn);
        }
    public String makeAfterWhere(String... critaires)
        {    StringBuilder where = new StringBuilder();
            Field [] field=this.getClass().getDeclaredFields();
            int i=1;
            for (String critarie : critaires) {
                    field[i].setAccessible(true);
                    if(!critarie.isEmpty() && critarie !=null) {
                        if (field[i].isAnnotationPresent(Column.class)) {
                            if (checkIfDateorString(field[i])) {
                                where.append(field[i].getAnnotation(Column.class).name()+"=").append("'"+critarie+"'").append(" and ");    
                            }
                        }
                        
                        else if (field[i].isAnnotationPresent(BaseObject.class)) {
                            where.append(field[i].getAnnotation(BaseObject.class).idBaseName()+"=").append(critarie).append(" and ");                                
                        }
                        
                        else {
                            where.append(field[i].getName()+"=").append(critarie).append(" and ");    
                        }
                     }
                    i++;
            }
            System.out.println("criteria = "+where.substring(0, where.length() - 1));
            return  where.substring(0, where.length() - 4);
        }

    public List<DB> recherche(String afterWhere,Connection conn) throws Exception {
        List<DB> results = new ArrayList<>();
        try {
            String tableName = getTableName();
            String query = String.format("SELECT * FROM %s where 1=1 AND %s", tableName,afterWhere);
    
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
    
                while (rs.next()) {
                    DB obj = this.getClass().getDeclaredConstructor().newInstance();
                    Field[] fields = this.getClass().getDeclaredFields();
    
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object value;
                        if (field.isAnnotationPresent(Column.class)) {
                            value = rs.getObject(field.getAnnotation(Column.class).name());
                        } else if (field.isAnnotationPresent(BaseObject.class)) {
                            value = ((DB) field.getType().getDeclaredConstructor().newInstance()).getById(conn, rs.getInt(field.getAnnotation(BaseObject.class).idBaseName()));
                        } else {
                            value = rs.getObject(field.getName());
                        }
    
                        // condition si le type est BigDecimal
                        if (value != null) {
                            if (field.getType() == double.class && value instanceof BigDecimal) {
                                value = ((BigDecimal) value).doubleValue();
                            } else if (field.getType() == int.class && value instanceof Integer) {
                                value = ((Integer) value).intValue();
                            } else if (field.getType() == short.class && value instanceof Integer) {
                                value = ((Integer) value).shortValue();
                            }
                        }
    
                        field.set(obj, value);
                    }
    
                    results.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la récupération des données.", e);
        }
        return results;

    } 

    
    public String makeWhere()
        {
            Field[] fields = this.getClass().getDeclaredFields();
            StringBuilder where = new StringBuilder();
            where.append("1=1 ");
            for (Field field : fields) {
                where.append("AND ");
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    if (field.isAnnotationPresent(Column.class)) {
                        where.append(field.getAnnotation(Column.class).name()).append(" = ? ");
                    } else {
                        where.append(field.getName()).append(" = ? ");
                    }
                    break;
                }
                else if (field.isAnnotationPresent(Column.class)) {
                    where.append(field.getAnnotation(Column.class).name()).append(" = ? ");
                } 
                else if (field.isAnnotationPresent(BaseObject.class)) {
                    try {
                        where.append(field.getAnnotation(BaseObject.class).idBaseName()).append(" = ? ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return where.toString();
        }

    public String getTableName() {
        Class<?> clazz = this.getClass();
        if (clazz.isAnnotationPresent(annotations.Table.class)) {
            return clazz.getAnnotation(annotations.Table.class).tableName();
            
        }

        return this.getClass().getSimpleName();
    }
    
    public Object inserer(Connection conn) throws SQLException {
        Object obj=null;
        try {
           
            conn.setAutoCommit(false);
            String tableName = getTableName();
            Field[] fields = this.getClass().getDeclaredFields();
    
            StringBuilder colonne = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
    
            int index = 0;  // Compteur pour vérifier la première colonne
            for (Field field : fields) {
                if (!field.isAnnotationPresent(PrimaryKey.class)) {
                    if (field.isAnnotationPresent(Column.class)) {
                        
                        colonne.append(field.getAnnotation(Column.class).name()).append(",");
                        placeholders.append("?,"); 
                    
                    }else if (field.isAnnotationPresent(BaseObject.class)) {
                        
                        colonne.append(field.getAnnotation(BaseObject.class).idBaseName()).append(",");
                        placeholders.append("?,");
                    }
                    else{
                        
                        colonne.append(field.getName()).append(",");
                        placeholders.append("?,");
                    }
                }
                index++;
            }
    
            String query = String.format("INSERT INTO %s (%s) VALUES (%s)",
                    tableName,
                    colonne.substring(0, colonne.length() - 1), // Retirer la dernière virgule
                    placeholders.substring(0, placeholders.length() - 1));
    
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                int paramIndex = 1;
                index = 0;  // Réinitialiser l'index pour la deuxième boucle
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (!field.isAnnotationPresent(PrimaryKey.class)) {    
                         if (field.isAnnotationPresent(BaseObject.class)) {
                            System.out.println(field.getType());
                            pstmt.setObject(paramIndex++, field.getType().getDeclaredMethod("getId").invoke(field.get(this)));
                        }  else {
                            pstmt.setObject(paramIndex++, field.get(this));
                        }
                       
                    }
                    index++;
                }

                pstmt.executeUpdate();
                conn.commit();
                List<DB>ob= selectAll(conn);
                return ob.get(ob.size()-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            System.out.println("Erreur lors de l'insertion.");
        }
    
        return obj;
    }
    
   public List<DB> selectAll(Connection conn) throws SQLException {
    List<DB> results = new ArrayList<>();
    try {
        String tableName = getTableName();
        String query = String.format("SELECT * FROM %s", tableName);

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DB obj = this.getClass().getDeclaredConstructor().newInstance();
                Field[] fields = this.getClass().getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value;
                    if (field.isAnnotationPresent(Column.class)) {
                        value = rs.getObject(field.getAnnotation(Column.class).name());
                    } else if (field.isAnnotationPresent(BaseObject.class)) {
                        value = ((DB) field.getType().getDeclaredConstructor().newInstance()).getById(conn, rs.getInt(field.getAnnotation(BaseObject.class).idBaseName()));
                    } else {
                        value = rs.getObject(field.getName());
                    }

                    // condition si le type est BigDecimal
                    if (value != null) {
                        if (field.getType() == double.class && value instanceof BigDecimal) {
                            value = ((BigDecimal) value).doubleValue();
                        } else if (field.getType() == int.class && value instanceof Integer) {
                            value = ((Integer) value).intValue();
                        } else if (field.getType() == short.class && value instanceof Integer) {
                            value = ((Integer) value).shortValue();
                        }
                    }

                    field.set(obj, value);
                }

                results.add(obj);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        throw new SQLException("Erreur lors de la récupération des données.", e);
    }
    return results;
}

    public void deleteById(Connection conn, int id) throws SQLException {
        try {
            conn.setAutoCommit(false);
            String tableName = getTableName();
            Field[] fields = this.getClass().getDeclaredFields();
    
            // Recherche du champ correspondant à l'ID
            String primaryKeyField = null;
            for (Field field : fields) {
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    primaryKeyField = field.getAnnotation(Column.class).name();
                    break;
                }
            }
    
            if (primaryKeyField == null) {
                throw new SQLException("Aucune colonne contenant 'id' trouvée dans la classe.");
            }
    
            String query = String.format("DELETE FROM %s WHERE %s = ?", tableName, primaryKeyField);
    
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, id); // Assigne l'ID passé en argument
                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " ligne(s) supprimée(s).");
            }
            
            conn.commit(); // Valide la transaction
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback(); // Annule la transaction en cas d'erreur
            System.out.println("Erreur lors de la suppression par ID.");
        }
    }
    public void updateById(Connection conn, int id) throws SQLException {
        try {
            conn.setAutoCommit(false);
            String tableName = getTableName();
            Field[] fields = this.getClass().getDeclaredFields();
    
            // Construction de la clause SET
            StringBuilder setClause = new StringBuilder();
            String primaryKeyField="";
            for (Field field : fields) {
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    primaryKeyField = field.getAnnotation(Column.class).name();
                
                }
                if (!field.isAnnotationPresent(PrimaryKey.class)) {
                    if (field.isAnnotationPresent(Column.class)) {
                        setClause.append(field.getAnnotation(Column.class).name()).append(" = ?, ");
                    
                    }else if (field.isAnnotationPresent(BaseObject.class)) {
                        setClause.append(field.getAnnotation(BaseObject.class).idBaseName()).append(" = ?, ");
                    }
                    else{
                        
                        setClause.append(field.getName()).append("= ?,");
                        
                    }
                }
            }
            

    
            // Supprimer la dernière virgule et espace
            String setClauseStr = setClause.substring(0, setClause.length() - 2);
    
            String query = String.format("UPDATE %s SET %s WHERE %s = ?", tableName, setClauseStr, primaryKeyField);
    
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                int parameterIndex = 1;
                
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (!field.isAnnotationPresent(PrimaryKey.class)) {    
                        if (field.isAnnotationPresent(BaseObject.class)) {
                           System.out.println(field.getType());
                           pstmt.setObject(parameterIndex++, field.getType().getDeclaredMethod("getId").invoke(field.get(this)));
                       }  else {
                           pstmt.setObject(parameterIndex++, field.get(this));
                       }
                      
                   }
                }
              
                // Ajout de l'ID comme dernier paramètre
                pstmt.setInt(parameterIndex, id);
    
                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " ligne(s) mise(s) à jour.");
            }
    
            conn.commit(); // Valide la transaction
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback(); // Annule la transaction en cas d'erreur
            System.out.println("Erreur lors de la mise à jour par ID.");
        }
    }
    public Object getById(Connection conn, int id) throws SQLException {
        try {
            String tableName = getTableName();
            Field[] fields = this.getClass().getDeclaredFields();
            
            // Find ID field name
            String idField = fields[0].getName();
            if (fields[0].isAnnotationPresent(Column.class)) {
                idField = fields[0].getAnnotation(Column.class).name();
            }
            
            String query = String.format("SELECT * FROM %s WHERE %s = ?", tableName, idField);
    
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, id);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Object obj = this.getClass().getDeclaredConstructor().newInstance();
                        
                        for (Field field : fields) {
                            field.setAccessible(true);
                            Object value;
                            if (field.isAnnotationPresent(Column.class)) {
                                value = rs.getObject(field.getAnnotation(Column.class).name());
                            } else if (field.isAnnotationPresent(BaseObject.class)) {
                                value = ((DB) field.getType().getDeclaredConstructor().newInstance()).getById(conn, rs.getInt(field.getAnnotation(BaseObject.class).idBaseName()));
                            } else {
                                value = rs.getObject(field.getName());
                            }
    
                            // condition si le type est BigDecimal
                            if (value != null) {
                                if (field.getType() == double.class && value instanceof BigDecimal) {
                                    value = ((BigDecimal) value).doubleValue();
                                } else if (field.getType() == int.class && value instanceof Integer) {
                                    value = ((Integer) value).intValue();
                                } else if (field.getType() == short.class && value instanceof Integer) {
                                    value = ((Integer) value).shortValue();
                                }
                            }
    
                            field.set(obj, value);
                        }
                        
                        return obj;
                    }
                }
            }
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération par ID.");
        }
        return null;
    }
    
    
    
}