package xcomp.ytemoi.ytemoiSmartWatch.Model

import org.bson.BsonDocument
import org.bson.BsonString

public class ThongSo( var tenThongSo:String,var chiso:String ,var kyhieu:String ){
    public var loai:String = ""

    fun toBsonDocument(): BsonDocument? {
        val bsonDocument = BsonDocument()
        bsonDocument.append("ten", BsonString(tenThongSo))
        bsonDocument.append("data", BsonString(chiso.toString()))
        if (loai != "") {
            bsonDocument.append("nguon", BsonString(loai))
            return bsonDocument
        }
        return null
    }
}