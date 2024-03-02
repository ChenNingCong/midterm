This is the midterm code for Ningcong Chen.
1. Database setup:
1.1 The database name is `account`. The login username is `chenningcong` and password is `12345678`. This setting can be changed by modifying the following code in `src/sql/MySQLConnector.java`. 
```
    static MySQLConnectionParams provideMySQLConnectionParams() {
        return new MySQLConnectionParams("jdbc:mysql://localhost:3306", "account", "chenningcong", "12345678");
    }
```
1.2 The database and the user can be created with the following sql statements in the root account (`sudo mysql -u root -p`):
```
CREATE USER chenningcong@localhost IDENTIFIED BY '12345678';
CREATE DATABASE `account` DEFAULT CHARACTER SET utf8mb4;
GRANT ALL PRIVILEGES ON account.* TO chenningcong@localhost;
```
1.3 By default, the table contains two accounts. **The `admin` account is used for login**. The table will be created automatically during the first time you execute the program.

| id | login | pincode | holdersname| balance | status |
|---| --- | --- | --- | --- | --- |
|1|admin|12345|XYZ|6000|Active|
|2|UserName2|88888|ABC|100|Disabled|

