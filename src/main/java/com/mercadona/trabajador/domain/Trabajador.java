package com.mercadona.trabajador.domain;

import com.mercadona.tienda.domain.Tienda;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "trabajadores")
public class Trabajador {

    private static final int HORAS_MAXIMAS = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni", unique = true, nullable = false, length = 9)
    private String dni;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "horas_disponibles", nullable = false)
    private int horasDisponibles;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tienda_id", nullable = false)
    private Tienda tienda;
    
    protected Trabajador() {
        // Constructor vacío requerido por JPA
    }
    
    public Trabajador(String dni, String nombre, int horasDisponibles, Tienda tienda) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        if (!isValidDniOrNie(dni)) {
            throw new IllegalArgumentException("El documento debe tener formato válido (DNI: 8 dígitos + letra, NIE: X/Y/Z + 7 dígitos + letra)");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        if (horasDisponibles < 0 || horasDisponibles > HORAS_MAXIMAS) {
            throw new IllegalArgumentException("Las horas disponibles deben estar entre 0 y " + HORAS_MAXIMAS);
        }
        if (tienda == null) {
            throw new IllegalArgumentException("La tienda no puede ser nula");
        }

        this.dni = dni;
        this.nombre = nombre;
        this.horasDisponibles = horasDisponibles;
        this.tienda = tienda;
    }
    
    public Long getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public int getHorasDisponibles() {
        return horasDisponibles;
    }

    public Tienda getTienda() {
        return tienda;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }
    
    public void setHorasDisponibles(int horasDisponibles) {
        if (horasDisponibles < 1 || horasDisponibles > 8) {
            throw new IllegalArgumentException("Las horas disponibles deben estar entre 1 y 8");
        }
        this.horasDisponibles = horasDisponibles;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Trabajador trabajador = (Trabajador) obj;
        return Objects.equals(dni, trabajador.dni);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    private boolean isValidDniOrNie(String documento) {
        return isValidDni(documento) || isValidNie(documento);
    }

    private boolean isValidDni(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }

        String numeros = dni.substring(0, 8);
        char letra = dni.charAt(8);

        if (!numeros.matches("\\d{8}")) {
            return false;
        }

        // Algoritmo de validación DNI español
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(numeros);
        char letraCalculada = letras.charAt(numero % 23);

        return Character.toUpperCase(letra) == letraCalculada;
    }

    private boolean isValidNie(String nie) {
        if (nie == null || nie.length() != 9) {
            return false;
        }

        char primerCaracter = Character.toUpperCase(nie.charAt(0));
        String numeros = nie.substring(1, 8);
        char letra = nie.charAt(8);

        if (primerCaracter != 'X' && primerCaracter != 'Y' && primerCaracter != 'Z') {
            return false;
        }

        if (!numeros.matches("\\d{7}")) {
            return false;
        }

        // Algoritmo de validación NIE español
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        
        // Convertir primera letra a número para el cálculo
        int primerDigito = 0;
        if (primerCaracter == 'X') primerDigito = 0;
        else if (primerCaracter == 'Y') primerDigito = 1;
        else if (primerCaracter == 'Z') primerDigito = 2;
        
        String numeroCompleto = primerDigito + numeros;
        int numero = Integer.parseInt(numeroCompleto);
        char letraCalculada = letras.charAt(numero % 23);

        return Character.toUpperCase(letra) == letraCalculada;
    }
}
