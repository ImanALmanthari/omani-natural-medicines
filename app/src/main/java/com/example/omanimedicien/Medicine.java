package com.example.omanimedicien;

import java.io.Serializable;

public class Medicine implements Serializable {
    private int id;
    private String name;
    private String image;
    private String compound;
    private String description;
    private String instructions;
    private double price;
    private String tempRange;
    private String ageLimit;
    private String warnings;

    public Medicine(int id, String name, String image, String compound, String description, 
                    String instructions, double price, String tempRange, String ageLimit, String warnings) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.compound = compound;
        this.description = description;
        this.instructions = instructions;
        this.price = price;
        this.tempRange = tempRange;
        this.ageLimit = ageLimit;
        this.warnings = warnings;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getCompound() { return compound; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public double getPrice() { return price; }
    public String getTempRange() { return tempRange; }
    public String getAgeLimit() { return ageLimit; }
    public String getWarnings() { return warnings; }
}
