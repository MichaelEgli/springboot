package ch.eglim.springboot.exercise.persistence.dto;

public record DepartmentSalaryStatistics(String departmentName, double averageSalary) {
}

/*
Ein record in Java ist eine spezielle Klasse, die für unveränderliche Datenobjekte gedacht ist. Die wichtigsten Unterschiede zu einer normalen Klasse sind:
record-Klassen sind per Definition immutable (unveränderlich).
Konstruktor, Getter, equals(), hashCode() und toString() werden automatisch generiert.
Felder sind automatisch final und private.
Weniger Boilerplate-Code, da keine manuellen Getter oder Konstruktoren nötig sind.
record kann keine explizite Vererbung nutzen (außer Interfaces).
Normale Klassen bieten volle Flexibilität (Vererbung, Mutable-Felder, eigene Methoden), benötigen aber mehr Code für Standardmethoden.
*/