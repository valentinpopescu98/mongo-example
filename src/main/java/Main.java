import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    static void create(MongoCollection<Document> collection, Document doc) {
        Document existingDoc = collection.find(new Document("name", doc.getString("name"))).first();
        if (existingDoc != null) {
            logger.warn("Duplicate product found: " + existingDoc.toJson());
            return;
        }

        collection.insertOne(doc);
        logger.info("Product inserted: " + doc.toJson());
    }

    static void readOne(MongoCollection<Document> collection, String docName) {
        Document foundDoc = collection.find(new Document("name", docName)).first();
        if (foundDoc == null) {
            logger.warn("No product found with name: " + docName);
            return;
        }

        System.out.println(foundDoc.toJson());
    }

    static void readAll(MongoCollection<Document> collection) {
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }

    static void update(MongoCollection<Document> collection, String docName, Document newDoc) {
        Document foundDoc = collection.find(new Document("name", docName)).first();
        if (foundDoc == null) {
            logger.warn("No product found with name: " + docName);
            return;
        }

        Document updateDoc = new Document("$set", newDoc);
        collection.updateOne(Filters.eq("name", docName), updateDoc);
        logger.info("Updated document with name '{}'", docName);
    }

    static void delete(MongoCollection<Document> collection, String docName) {
        Document deletedDoc = collection.findOneAndDelete(new Document("name", docName));
        if (deletedDoc == null) {
            logger.warn("No product found with name: " + docName);
            return;
        }

        logger.info("Product deleted: " + deletedDoc.toJson());
    }

    public static void main(String[] args) {
        // Create a new MongoClient object to connect to the server
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            logger.info("Connected to MongoDB");

            // Connect to the database
            MongoDatabase database = mongoClient.getDatabase("store");
            logger.info("Connected to database: store");

            // Connect to the collection
            MongoCollection<Document> collection = database.getCollection("product");
            logger.info("Connected to collection: product");

            Document iphone = new Document("name", "Telefon mobil Apple iPhone 15 Pro Max, 256GB, 5G, Natural Titanium")
                    .append("description", "Telefon mobil Apple iPhone 15 Pro Max, 256GB, 5G, Natural Titanium")
                    .append("price", "6.599,99 RON")
                    .append("color", "Natural Titanium")
                    .append("memory", "256 GB");

            Document samsung256 = new Document("name", "Telefon mobil Samsung Galaxy S24 Ultra, Dual SIM, 12GB RAM, 256GB, 5G, Titanium Black")
                    .append("price", "5.899,99 RON")
                    .append("color", "Negru titan")
                    .append("memory", "256 GB");

            Document samsung512 = new Document("name", "Telefon mobil Samsung Galaxy S24 Ultra, Dual SIM, 12GB RAM, 512GB, 5G, Titanium Black")
                    .append("price", "6.899,99 RON")
                    .append("SRP", "8.799,99 RON")
                    .append("color", "Roz titan")
                    .append("memory", "512 GB");

            create(collection, iphone);
            readAll(collection);
            create(collection, iphone);
            create(collection, samsung256);
            readAll(collection);
            readOne(collection, "iPhone 15 Pro Max");
            readOne(collection, iphone.getString("name"));
            delete(collection, iphone.getString("name"));
            update(collection, samsung256.getString("name"), samsung512);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
