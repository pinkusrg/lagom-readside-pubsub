import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.sub.impl.eventsourcing.{MessageSaved, MessageState, SaveNewMessage}

object MessageSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[SaveNewMessage],
    JsonSerializer[MessageSaved],
    JsonSerializer[MessageState]
  )
}
