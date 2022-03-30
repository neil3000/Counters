package rahmouni.neil.counters.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import rahmouni.neil.counters.CounterStyle
import rahmouni.neil.counters.IncrementType
import rahmouni.neil.counters.IncrementValueType
import rahmouni.neil.counters.ResetType
import java.sql.Date

@Database(
    entities = [Counter::class, Increment::class],
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ],
    exportSchema = true,
    version = 3
)
abstract class CountersDatabase : RoomDatabase() {
    abstract fun countersListDao(): CountersListDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: CountersDatabase? = null

        fun getInstance(context: Context): CountersDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CountersDatabase::class.java,
                    "counters_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

@Entity
data class Counter(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "style") val style: CounterStyle = CounterStyle.DEFAULT,
    @ColumnInfo(name = "has_minus") val hasMinus: Boolean = false,
    @ColumnInfo(name = "increment_type") val incrementType: IncrementType = IncrementType.VALUE,
    @ColumnInfo(name = "increment_value_type") val incrementValueType: IncrementValueType = IncrementValueType.VALUE,
    @ColumnInfo(name = "increment_value") val incrementValue: Int = 1,
    @ColumnInfo(
        name = "reset_type",
        defaultValue = "NEVER"
    ) val resetType: ResetType = ResetType.NEVER,
)

data class CounterAugmented(
    @ColumnInfo(name = "uid") val uid: Int = 0,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "style") val style: CounterStyle = CounterStyle.DEFAULT,
    @ColumnInfo(name = "has_minus") val hasMinus: Boolean = false,
    @ColumnInfo(name = "increment_type") val incrementType: IncrementType = IncrementType.VALUE,
    @ColumnInfo(name = "increment_value_type") val incrementValueType: IncrementValueType = IncrementValueType.VALUE,
    @ColumnInfo(name = "increment_value") val incrementValue: Int = 1,
    @ColumnInfo(
        name = "reset_type",
        defaultValue = "NEVER"
    ) val resetType: ResetType = ResetType.NEVER,

    @ColumnInfo(name = "total_count") val totalCount: Int = 0,
    @ColumnInfo(name = "last_increment") val lastIncrement: Int = 1,
    @ColumnInfo(name = "count") val count: Int = 0
) {
    fun toCounter(): Counter {
        return Counter(
            uid = uid,
            displayName = displayName,
            style = style,
            hasMinus = hasMinus,
            incrementType = incrementType,
            incrementValueType = incrementValueType,
            incrementValue = incrementValue,
            resetType = resetType
        )
    }
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = Counter::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("counterID"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Increment(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "counterID") val counterID: Int,
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(
        name = "timestamp",
        defaultValue = "CURRENT_TIMESTAMP"
    ) val timestamp: String = "CURRENT_TIMESTAMP"
)

data class CounterWithIncrements(
    @Embedded val counter: CounterAugmented,
    @Relation(
        parentColumn = "uid",
        entityColumn = "counterID"
    )
    val increments: List<Increment>
)

data class IncrementGroup(
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "uids") val uids: String = "test",
)

@Dao
interface CountersListDao {
    @Query(
        "SELECT counter.*,sub.total_count,sub2.last_increment,sub3.count FROM counter LEFT JOIN (SELECT counterID, SUM(value) as total_count FROM increment GROUP BY counterID) as sub ON sub.counterID=counter.uid LEFT JOIN (SELECT counterID,value as last_increment,Max(timestamp) as timestamp FROM increment GROUP BY counterID) as sub2 ON sub2.counterID=counter.uid LEFT JOIN (SELECT counterID, SUM(value) as count FROM increment  JOIN counter ON counter.uid=increment.counterID WHERE (counter.reset_type='NEVER') OR (counter.reset_type='DAY' AND date(timestamp, 'localtime') >= date('now', 'localtime')) OR (counter.reset_type='WEEK' AND date(timestamp, 'localtime') >= date('now', 'localtime', 'weekday 0', '-7 days')) OR (counter.reset_type='MONTH' AND date(timestamp, 'localtime') >= date('now', 'localtime', 'start of month')) GROUP BY counterID) as sub3 ON sub3.counterID=counter.uid"
    )
    fun getAll(): Flow<List<CounterAugmented>>

    @Transaction
    @Query(
        "SELECT counter.*,sub.total_count,sub2.last_increment,sub3.count FROM counter LEFT JOIN (SELECT counterID, SUM(value) as total_count FROM increment GROUP BY counterID) as sub ON sub.counterID=counter.uid LEFT JOIN (SELECT counterID,value as last_increment,Max(timestamp) as timestamp FROM increment GROUP BY counterID) as sub2 ON sub2.counterID=counter.uid LEFT JOIN (SELECT counterID, SUM(value) as count FROM increment  JOIN counter ON counter.uid=increment.counterID WHERE (counter.reset_type='NEVER') OR (counter.reset_type='DAY' AND date(timestamp, 'localtime') >= date('now', 'localtime')) OR (counter.reset_type='WEEK' AND date(timestamp, 'localtime') >= date('now', 'localtime', 'weekday 0', '-7 days')) OR (counter.reset_type='MONTH' AND date(timestamp, 'localtime') >= date('now', 'localtime', 'start of month')) GROUP BY counterID) as sub3 ON sub3.counterID=counter.uid WHERE counter.uid=:counterID"
    )
    fun getCounterWithIncrements(counterID: Int): Flow<CounterWithIncrements>

    @Query(
        "SELECT SUM(value) as count, date(timestamp, :groupQuery1, :groupQuery2) as date, GROUP_CONCAT(uid, ',') AS uids FROM increment WHERE counterID=:counterID GROUP BY date(timestamp, 'localtime', :groupQuery1, :groupQuery2) ORDER BY timestamp DESC"
    )
    fun getCounterIncrementGroups(counterID: Int, groupQuery1: String, groupQuery2: String): Flow<List<IncrementGroup>>

    @Query("SELECT * FROM counter WHERE uid=:counterID")
    fun getCounter(counterID: Int): Flow<Counter>

    @Query("INSERT INTO increment (value, counterID) VALUES(:value, :counterID)")
    suspend fun addIncrement(value: Int, counterID: Int): Long

    @Insert
    suspend fun addCounter(counter: Counter): Long

    @Delete
    suspend fun deleteIncrement(increment: Increment)

    @Query("DELETE FROM counter WHERE uid=:counterID")
    suspend fun deleteCounterById(counterID: Int)

    @Update
    suspend fun updateCounter(counter: Counter)
}