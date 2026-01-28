
// Napiši Java konzolnu aplikaciju koja rješava sljedeći problem.
//
// Kod unosa stavki (tablica Stavka) zabunom je unesena cijena po komadu za stavke sa id-evima 8 i 9.
// Cijenu po komadu za stavku sa id-em 8 treba povećati za 10, a cijenu po komadu stavke sa id-em 9 treba smanjiti za 10
// Osigurati da se obje izmjene dogode u transakciji, tj ili se izvrše obje izmjene ili niti jedna
// Ispisati vrijednost stavki prije i nakon promjene

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class Zadatak {
    public static void main(String[] args) {
        DataSource dataSource = createDataSource();

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Uspjesno spajanje na bazu!");

//            CREATE OR ALTER PROC IzmjeniCijene @IDStavka1 INT, @IDStavka2 INT
//                    AS
//            BEGIN
//            BEGIN TRY
//            UPDATE Stavka SET CijenaPoKomadu = CijenaPoKomadu + 10  WHERE IDStavka = @IDStavka1
//                    UPDATE Stavka SET CijenaPoKomadu = CijenaPoKomadu - 10  WHERE IDStavka = @IDStavka2
//                    END TRY
//            BEGIN CATCH
//            PRINT 'Greska pri izvrsavanju izmjene'
//            ;THROW;
//            END CATCH
//            END

            try (CallableStatement cs = connection.prepareCall("{call IzmjeniCijene(?,?)}");
                 Statement stmt = connection.createStatement();
            ){
                connection.setAutoCommit(false);
                ResultSet resultSet = stmt.executeQuery("SELECT * FROM Stavka WHERE IDStavka IN (8, 9)");

                System.out.println("Vrijednost stavki: ");
                System.out.println("ID | Cijena Po Komadu");
                while (resultSet.next()) {
                    System.out.printf(
                            "%d  | %s\n",
                            resultSet.getInt("IDStavka"),
                            resultSet.getString("CijenaPoKomadu")
                    );
                }

                cs.setInt(1, 8);
                cs.setInt(2, 9);
                cs.executeUpdate();

                connection.commit();
                System.out.println("Transakcija izvrsena!");

                resultSet = stmt.executeQuery("SELECT * FROM Stavka WHERE IDStavka IN (8, 9)");
                System.out.println("Vrijednost stavki: ");
                System.out.println("ID | Cijena Po Komadu");
                while (resultSet.next()) {
                    System.out.printf(
                            "%d  | %s\n",
                            resultSet.getInt("IDStavka"),
                            resultSet.getString("CijenaPoKomadu")
                    );
                }

                resultSet.close();

            } catch(SQLException e){
                System.err.println("Transakcija ponistena!");
                connection.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu");
            e.printStackTrace();
        }


    }

    // DataSource
    private static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("AdventureWorksOBP");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);
        return ds;
    }


}
