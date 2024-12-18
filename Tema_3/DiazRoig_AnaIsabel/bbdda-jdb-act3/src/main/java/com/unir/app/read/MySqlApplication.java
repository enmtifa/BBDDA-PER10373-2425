package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            selectAllEmployeesOfDepartment(connection, "d001");
            selectAllEmployeesOfDepartment(connection, "d002");
            selectEmployeesByDepartment(connection);
            selectAverageSalaryByDepartment(connection);
            selectAverageSalaryByDepartmentAndGender(connection);


        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando Statement.
     * Statement es la forma más básica de ejecutar consultas a la base de datos.
     * Es la más insegura, ya que no se protege de ataques de inyección SQL.
     * No obstante es útil para sentencias DDL.
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select * from employees");

        while (employees.next()) {
            log.debug("Employee: {} {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"));
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando PreparedStatement.
     * PreparedStatement es la forma más segura de ejecutar consultas a la base de datos.
     * Se protege de ataques de inyección SQL.
     * Es útil para sentencias DML.
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployeesOfDepartment(Connection connection, String department)
            throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement(
                "select count(*) as 'Total'\n" +
                "from employees emp\n" +
                "inner join dept_emp dep_rel on emp.emp_no = dep_rel.emp_no\n" +
                "inner join departments dep on dep_rel.dept_no = dep.dept_no\n" +
                "where dep_rel.dept_no = ?;\n");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Empleados del departamento {}: {}",
                    department,
                    employees.getString("Total"));
        }
    }

    /**
     * Consulta 1: Obtener el número de empleados por departamento
     * Usando PreparedStatement
     * @param connection : Conexion a la base de datos
     * @throws SQLException : Excepcion SQL
     */
    private static void selectEmployeesByDepartment(Connection connection)
            throws SQLException {

        PreparedStatement selectEmployees = connection.prepareStatement
                    ("select dep.dept_name, count(*) as 'Total'\n" +
                "from employees emp\n" +
                "inner join dept_emp dep_rel on emp.emp_no = dep_rel.emp_no\n" +
                "inner join departments dep on dep_rel.dept_no = dep.dept_no\n" +
                "group by dep.dept_name;");
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Departamento: {}, Empleados: {}",
                    employees.getString("dept_name"),
                    employees.getString("Total"));
        }
    }

    /**
     * Consulta 2: Obtener el salario medio por departamento
     * Usando PreparedStatement
     * @param connection : Conexion a la base de datos
     * @throws SQLException : Excepcion SQL
     */
    private static void selectAverageSalaryByDepartment(Connection connection)
            throws SQLException {

        PreparedStatement selectEmployees = connection.prepareStatement
                ("select dep.dept_name, FORMAT(avg(sal.salary),2) " +
                        "as 'Salario medio', emp.gender\n" +
                        "from employees emp\n" +
                        "inner join dept_emp dep_rel on emp.emp_no = dep_rel.emp_no\n" +
                        "inner join departments dep on dep_rel.dept_no = dep.dept_no\n" +
                        "inner join salaries sal on emp.emp_no = sal.emp_no\n" +
                        "group by dep.dept_name, emp.gender");
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Departamento: {}, Salario medio: {}, Género: {}",
                    employees.getString("dept_name"),
                    employees.getString("Salario medio"),
                    employees.getString("gender"));
        }
    }
    /**
     * Consulta 3: Obtener el salario medio por departamento y género
     * Usando PreparedStatement
     * @param connection : Conexion a la base de datos
     * @throws SQLException : Excepcion SQL
     */
    private static void selectAverageSalaryByDepartmentAndGender(Connection connection)
            throws SQLException {

        PreparedStatement selectEmployees = connection.prepareStatement
                ("select dep.dept_name, FORMAT(avg(sal.salary), 2) as 'Salario medio', emp.gender\n" +
                        "from employees emp\n" +
                        "inner join dept_emp dep_rel on emp.emp_no = dep_rel.emp_no\n" +
                        "inner join departments dep on dep_rel.dept_no = dep.dept_no\n" +
                        "inner join salaries sal on emp.emp_no = sal.emp_no\n" +
                        "group by dep.dept_name, emp.gender");
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Departamento: {}, Salario medio: {}, Género: {}",
                    employees.getString("dept_name"),
                    employees.getString("Salario medio"),
                    employees.getString("gender"));
        }
    }
}
