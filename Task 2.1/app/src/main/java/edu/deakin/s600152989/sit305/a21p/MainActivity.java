package edu.deakin.s600152989.sit305.a21p;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public enum UnitCategory {
        LENGTH,
        WEIGHT,
        TEMPERATURE
    }

    public enum LengthUnits {
        CENTIMETRES("Centimetre", 0.01),
        INCHES("Inch", 0.0254),
        FEET("Foot", 0.3048),
        METRES("Metre", 1.0),
        YARDS("Yard", 0.9144),
        KILOMETRES("Kilometre", 1000.0),
        MILES("Mile", 1609.34);

        private final String name;
        private final double conversionFactor;
        LengthUnits(String name, double conversionFactor) {
            this.name = name;
            this.conversionFactor = conversionFactor;
        }

        public String getName() {
            return name;
        }

        public double getConversionFactor(){
            return conversionFactor;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    public enum WeightUnits {
        MILLIGRAMS("Milligrams", 0.000001),
        GRAMS("Gram", 0.001),
        OUNCES("Ounce", 0.0283495),
        POUNDS("Pound", 0.453592),
        KILOGRAMS("Kilogram", 1.0),
        TON("Ton", 907.185),
        TONNE("Tonne", 1000.0);

        private final String name;
        private final double conversionFactor;
        WeightUnits(String name, double conversionFactor) {
            this.name = name;
            this.conversionFactor = conversionFactor;
        }

        public String getName() {
            return name;
        }

        public double getConversionFactor(){
            return conversionFactor;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    public enum TemperatureUnits {
        CELSIUS("Celsius"),
        KELVIN("Kelvin"),
        FAHRENHEIT("Fahrenheit");

        private final String name;

        public String getName() {
            return name;
        }

        TemperatureUnits(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public EditText inputValue;
    public TextView outputValue;
    public Spinner unitCategorySpinner, inputUnitSpinner, outputUnitSpinner;
    public Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        // initialise UI components
        unitCategorySpinner = findViewById(R.id.unitCategorySpinner);
        inputUnitSpinner = findViewById(R.id.inputUnitSpinner);
        outputUnitSpinner = findViewById(R.id.outputUnitSpinner);
        inputValue = findViewById(R.id.inputValue);
        outputValue = findViewById(R.id.outputValue);
        convertButton = findViewById(R.id.convertButton);

        // Set up spinners
        ArrayAdapter<UnitCategory> categoryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UnitCategory.values() );
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitCategorySpinner.setAdapter(categoryArrayAdapter);

        unitCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UnitCategory selectedCategory = UnitCategory.values()[position];
                updateUnitSpinners(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected, if necessary
            }
        });


        //
        convertButton.setOnClickListener(v -> {

            String inputText = inputValue.getText().toString();

            double value;
            try {
                value = Double.parseDouble(inputValue.getText().toString());
            } catch (NumberFormatException e) {
                outputValue.setText("Invalid input! Please enter a valid number.");
                return;
            }

            if (value < 0) {
                UnitCategory selectedCategory = (UnitCategory) unitCategorySpinner.getSelectedItem();

                if (selectedCategory == UnitCategory.LENGTH || selectedCategory == UnitCategory.WEIGHT) {
                    outputValue.setText("Negative values are not allowed for length or weight conversions.");
                    return;
                }
            }

            Object inputUnit = (Object) inputUnitSpinner.getSelectedItem();
            Object outputUnit = (Object) outputUnitSpinner.getSelectedItem();

            if((inputUnit instanceof LengthUnits && outputUnit instanceof WeightUnits) ||
                    (inputUnit instanceof LengthUnits && outputUnit instanceof TemperatureUnits) ||
                    (inputUnit instanceof WeightUnits && outputUnit instanceof LengthUnits) ||
                    (inputUnit instanceof WeightUnits && outputUnit instanceof TemperatureUnits) ||
                    (inputUnit instanceof TemperatureUnits && outputUnit instanceof LengthUnits) ||
                    (inputUnit instanceof TemperatureUnits && outputUnit instanceof WeightUnits)) {

                outputValue.setText("Incompatible units for conversion!");
                return;
            }

            double result = Converter.convert(inputUnit, outputUnit, value);
            outputValue.setText(String.format("Converted Value: %.2f", result));

        });

        updateUnitSpinners(UnitCategory.LENGTH);


    }

    private void updateUnitSpinners(UnitCategory selectedCategory) {
        ArrayAdapter<?> adapter;
        switch (selectedCategory) {
            case LENGTH:
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LengthUnits.values());
                break;
            case WEIGHT:
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, WeightUnits.values());
                break;
            case TEMPERATURE:
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TemperatureUnits.values());
                break;
            default:
                return;
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputUnitSpinner.setAdapter(adapter);
        outputUnitSpinner.setAdapter(adapter);

        outputUnitSpinner.setEnabled(true);
    }

    public static class Converter {
        public static double convert(Object inputUnit, Object outputUnit, double value) {
            if (inputUnit instanceof TemperatureUnits && outputUnit instanceof TemperatureUnits) {
                return convertTemperature((TemperatureUnits) inputUnit, (TemperatureUnits) outputUnit, value);
            } else if (inputUnit instanceof LengthUnits && outputUnit instanceof LengthUnits) {
                return convertLength((LengthUnits) inputUnit, (LengthUnits) outputUnit, value);
            } else if (inputUnit instanceof WeightUnits && outputUnit instanceof WeightUnits) {
                return convertWeight((WeightUnits) inputUnit, (WeightUnits) outputUnit, value);
            }
            throw new IllegalArgumentException("Invalid unit types for conversion");

        }

        private static double convertWeight(WeightUnits inputUnit, WeightUnits outputUnit, double value) {
            double valueInKilograms = value * inputUnit.getConversionFactor();
            return valueInKilograms / outputUnit.getConversionFactor();
        }

        public static double convertTemperature(TemperatureUnits inputUnit, TemperatureUnits outputUnit, double value) {

            if (inputUnit == outputUnit){
                return value;
            }

            double celsiusValue = toCelsius(inputUnit, value);

            return fromCelsius(outputUnit, celsiusValue);
        }

        private static double toCelsius(TemperatureUnits unit, double value) {
            switch (unit) {
                case CELSIUS:
                    return value;
                case FAHRENHEIT:
                    return (value - 32) * 5 / 9;
                case KELVIN:
                    return value - 273.15;
                default:
                    throw new IllegalArgumentException("Unknown temperature unit");
            }
        }

        private static double fromCelsius(TemperatureUnits unit, double celsiusValue){
            switch(unit) {
                case CELSIUS:
                    return celsiusValue;
                case FAHRENHEIT:
                    return (celsiusValue * 9 /5) +32;
                case KELVIN:
                    return celsiusValue + 273.15;
                default:
                    throw new IllegalArgumentException("Unknown temperature unit");
            }
        }

        public static double convertLength(LengthUnits inputUnit, LengthUnits outputUnit, double value){
            double valueInMetres  = value * inputUnit.getConversionFactor();
            return valueInMetres / outputUnit.getConversionFactor();
        }
    }
}