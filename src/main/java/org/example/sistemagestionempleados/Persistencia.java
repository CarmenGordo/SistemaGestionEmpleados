package org.example.sistemagestionempleados;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Persistencia {

    // Métodos para guardar y cargar empleados en formato binario
    public static void guardarEmpleadosBinario(List<Empleado> empleados, String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(empleados);
        }
    }

    public static List<Empleado> cargarEmpleadosBinario(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Empleado>) ois.readObject();
        }
    }

    // Métodos para exportar e importar XML

    @XmlRootElement(name = "empleados")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class EmpleadosList {
        @XmlElement(name = "empleado")
        private List<Empleado> empleados = new ArrayList<>();

        public List<Empleado> getEmpleados() {
            return empleados;
        }

        public void setEmpleados(List<Empleado> empleados) {
            this.empleados = empleados;
        }
    }

    public static void exportarXML(List<Empleado> empleados, String archivo) throws JAXBException {
        EmpleadosList listaEmpleados = new EmpleadosList();
        listaEmpleados.setEmpleados(empleados);

        JAXBContext context = JAXBContext.newInstance(EmpleadosList.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(listaEmpleados, new File(archivo));
    }

    public static List<Empleado> importarXML(String archivo) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(EmpleadosList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        EmpleadosList listaEmpleados = (EmpleadosList) unmarshaller.unmarshal(new File(archivo));
        return listaEmpleados.getEmpleados();
    }

    // Métodos para exportar e importar JSON
    public static void exportarJSON(List<Empleado> empleados, String archivo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(archivo), empleados);
    }

    public static List<Empleado> importarJSON(String archivo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(archivo), mapper.getTypeFactory().constructCollectionType(List.class, Empleado.class));
    }
}
