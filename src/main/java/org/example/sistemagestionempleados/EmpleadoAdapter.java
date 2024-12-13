package org.example.sistemagestionempleados;
import com.google.gson.*;

import java.lang.reflect.Type;

public class EmpleadoAdapter implements JsonSerializer<Empleado>, JsonDeserializer<Empleado> {

    @Override
    public JsonElement serialize(Empleado empleado, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", empleado.getId());
        jsonObject.addProperty("nombre", empleado.getNombre());
        jsonObject.addProperty("apellidos", empleado.getApellidos());
        jsonObject.addProperty("departamento", empleado.getDepartamento());
        jsonObject.addProperty("sueldo", empleado.getSueldo());
        return jsonObject;
    }

    @Override
    public Empleado deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();
        String nombre = jsonObject.get("nombre").getAsString();
        String apellidos = jsonObject.get("apellidos").getAsString();
        String departamento = jsonObject.get("departamento").getAsString();
        double sueldo = jsonObject.get("sueldo").getAsDouble();
        return new Empleado(id, nombre, apellidos, departamento, sueldo);
    }
}
