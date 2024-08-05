package thuan.dev.models.products;

import thuan.dev.config.MyConnection;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductImple implements ProductDAO {

    private Connection conn = MyConnection.getConnection();
    private List<Product> products = new ArrayList<>();

    @Override
    public void updateProductStock(int productID, int stock) {
        try {
            PreparedStatement statement = conn.prepareStatement("UPDATE product set stock = ? where productID = ?");
            statement.setInt(1, stock);
            statement.setInt(2, productID);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Dùng để update lại Stock trong sản lượng
    @Override
    public int getProductStock(int productID) {
        int stock = 0;
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT stock FROM product WHERE productID = ?");
            statement.setInt(1, productID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stock = resultSet.getInt("stock");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stock;
    }
    //Tìm sản phẩm ở đâu bằng productID

    @Override
    public List<Product> show() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = MyConnection.getConnection()) {
            PreparedStatement ptmt = conn.prepareStatement("SELECT product.productID, product.productName, product.categoryID, product.brandID, product.price, product.images, product.stock, "
                    + "brand.brandName, category.categoryName "
                    + "FROM product "
                    + "INNER JOIN brand ON product.brandID = brand.brandID "
                    + "INNER JOIN category ON product.categoryID = category.categoryID");

            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductID(rs.getInt("productID"));
                product.setProductName(rs.getString("productName"));
                product.setCategoryName(rs.getString("categoryName"));
                product.setBrandName(rs.getString("brandName"));
                product.setPrice(rs.getDouble("price"));
                product.setImages(rs.getString("images"));
                product.setStock(rs.getInt("stock"));
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    @Override
    public boolean addProduct(Product pro) {
        try {
            PreparedStatement ptmt = conn.prepareStatement("INSERT INTO product(productName,categoryID,brandID,price,images,stock) VALUES (?,?,?,?,?,?)");
            ptmt.setString(1, pro.getProductName());
            ptmt.setInt(2, pro.getCategoryID());
            ptmt.setInt(3, pro.getBrandID());
            ptmt.setObject(4, pro.getPrice());
            ptmt.setString(5, pro.getImages());
            ptmt.setInt(6, pro.getStock());
            int check = ptmt.executeUpdate();
            return check > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProduct(Product pro) {
        try {
            PreparedStatement ptmt = conn.prepareStatement(
                    "UPDATE product SET productName = ?, categoryID = ?, brandID = ?, price = ?, images = ?, stock = ? WHERE productID = ?");
            ptmt.setString(1, pro.getProductName());
            ptmt.setInt(2, pro.getCategoryID());
            ptmt.setInt(3, pro.getBrandID());
            ptmt.setDouble(4, pro.getPrice());
            ptmt.setString(5, pro.getImages());
            ptmt.setInt(6, pro.getStock());
            ptmt.setInt(7, pro.getProductID());
            ptmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement ptmt = conn.prepareStatement("SELECT product.productID, product.productName, product.categoryID, product.brandID, product.price, product.images, product.stock, "
                    + "brand.brandName, category.categoryName "
                    + "FROM product "
                    + "INNER JOIN brand ON product.brandID = brand.brandID "
                    + "INNER JOIN category ON product.categoryID = category.categoryID where productName LIKE ?");

            String search = "%" + keyword + "%";
            ptmt.setString(1, search);
            try {
                ResultSet rs = ptmt.executeQuery();
                while (rs.next()) {
                    Product pro = new Product();
                    pro.setProductID(rs.getInt("productID"));
                    pro.setProductName(rs.getString("productName"));
                    pro.setCategoryName(rs.getString("categoryName"));
                    pro.setBrandName(rs.getString("brandName"));
                    pro.setPrice(rs.getDouble("price"));
                    pro.setImages(rs.getString("images"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    @Override
    public boolean deleteProduct(Integer productID) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM product WHERE productID = ?")) {
            stmt.setInt(1, productID);
            int check = stmt.executeUpdate();
            return check > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
