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

    //  We need an enum of the unit categories so we can tell the app
    //  to update the spinners to match the dimension we're measuring
    public enum UnitCategory {
        LENGTH,
        WEIGHT,
        TEMPERATURE
    }

    // all our distance units, note the values there are how we tell the converter how to
    // do the work converting it, we use the metre as the central point
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

    // all our mass units, note the values there are how we tell the converter how to
    // do the work, everything is valued based on its relative proportion to a kilogram
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

    // note that unlike the others, there are no values assigned.
    // this is because we need to some math for these conversions
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

    //  
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

        // initialise the UI components
        unitCategorySpinner = findViewById(R.id.unitCategorySpinner);
        inputUnitSpinner = findViewById(R.id.inputUnitSpinner);
        outputUnitSpinner = findViewById(R.id.outputUnitSpinner);
        inputValue = findViewById(R.id.inputValue);
        outputValue = findViewById(R.id.outputValue);
        convertButton = findViewById(R.id.convertButton);

        // Set up our spinners
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


        //  build the logic for pressing the convert button
        convertButton.setOnClickListener(v -> {

            // This doesn't get called anymore
            //String inputText = inputValue.getText().toString();
            
            //  Here we are trying to prevent non-numerical inputs, but it should allow minus symbol
            double value;
            try {
                value = Double.parseDouble(inputValue.getText().toString());
            } catch (NumberFormatException e) {
                outputValue.setText("Invalid input! Please enter a valid number.");
                return;
            }

            //  We need negatives for temperature but its nonsense for negative length or weight units
            if (value < 0) {
                UnitCategory selectedCategory = (UnitCategory) unitCategorySpinner.getSelectedItem();

                if (selectedCategory == UnitCategory.LENGTH || selectedCategory == UnitCategory.WEIGHT) {
                    outputValue.setText("Negative values are not allowed for length or weight conversions.");
                    return;
                }
            }

            Object inputUnit = (Object) inputUnitSpinner.getSelectedItem();
            Object outputUnit = (Object) outputUnitSpinner.getSelectedItem();

            // A check to ensure we don't try to do incompatible conversion
            if((inputUnit instanceof LengthUnits && outputUnit instanceof WeightUnits) ||
                    (inputUnit instanceof LengthUnits && outputUnit instanceof TemperatureUnits) ||
                    (inputUnit instanceof WeightUnits && outputUnit instanceof LengthUnits) ||
                    (inputUnit instanceof WeightUnits && outputUnit instanceof TemperatureUnits) ||
                    (inputUnit instanceof TemperatureUnits && outputUnit instanceof LengthUnits) ||
                    (inputUnit instanceof TemperatureUnits && outputUnit instanceof WeightUnits)) {

                outputValue.setText("Incompatible units for conversion!");
                return;
            }

            // The output of our conversion being sent to the textView, at present doesn't include
            // any of the symbols for the units converted
            double result = Converter.convert(inputUnit, outputUnit, value);
            outputValue.setText(String.format("Converted Value: %.2f", result));

        });

        //  sets the default starting setting to length
        updateUnitSpinners(UnitCategory.LENGTH);
    }

    //  this lets us update the spinners to match what measure we're working with
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

        //  only enables the output unit spinner once the selection is made
        //  as another check to prevent incompatible conversions
        outputUnitSpinner.setEnabled(true);
    }

    //  This is where most of the stress has been
    public static class Converter {

        // This looks at both requested units and calls the conversion logic if they match
        public static double convert(Object inputUnit, Object outputUnit, double value) {
            if (inputUnit instanceof TemperatureUnits && outputUnit instanceof TemperatureUnits) {
                return convertTemperature((TemperatureUnits) inputUnit, (TemperatureUnits) outputUnit, value);
            } else if (inputUnit instanceof LengthUnits && outputUnit instanceof LengthUnits) {
                return convertLength((LengthUnits) inputUnit, (LengthUnits) outputUnit, value);
            } else if (inputUnit instanceof WeightUnits && outputUnit instanceof WeightUnits) {
                return convertWeight((WeightUnits) inputUnit, (WeightUnits) outputUnit, value);
            }
            //  Again we need to have an exception to catch incompatible pairings
            //  Even though we filter this above, we still need it just incase
            throw new IllegalArgumentException("Invalid unit types for conversion");

        }

        // This does weight conversion, we use kilograms as the reference point, so everything
        // is converted to kilograms first and then to whatever the selected output unit is.
        private static double convertWeight(WeightUnits inputUnit, WeightUnits outputUnit, double value) {
            double valueInKilograms = value * inputUnit.getConversionFactor();
            return valueInKilograms / outputUnit.getConversionFactor();
        }

        //  This is the start of converting temperature, unlike the other units, temperature requires
        //  some math to derive the conversions involving Fahrenheit because Fahrenheit is a stupid system
        public static double convertTemperature(TemperatureUnits inputUnit, TemperatureUnits outputUnit, double value) {

            //  no need to convert if its the same unit.
            if (inputUnit == outputUnit){
                return value;
            }

            //  we transform everything to Celsius as a base
            double celsiusValue = toCelsius(inputUnit, value);

            // returns the output of the method that does the conversion
            return fromCelsius(outputUnit, celsiusValue);
        }

        //  converts the input to celsius so we have a standardised starting point for calculation
        private static double toCelsius(TemperatureUnits unit, double value) {
            switch (unit) {
                case CELSIUS:
                    return value;  //  no changes
                case FAHRENHEIT:
                    return (value - 32) * 5 / 9; //  makes Fahrenheit into a Celsius value
                case KELVIN:
                    return value - 273.15; // Kelvin units and Celsius units share the same scale but different 0 points
                default:
                    throw new IllegalArgumentException("Unknown temperature unit"); //  again we need to have an escape
            }
        }

        //  Here we determine our output value by converting the celsius number we made above
        private static double fromCelsius(TemperatureUnits unit, double celsiusValue){
            switch(unit) {
                case CELSIUS:
                    return celsiusValue;  //  no changes because its already Celsius
                case FAHRENHEIT:
                    return (celsiusValue * 9 /5) +32; // alters the celsiusValue to a Fahrenheit number
                case KELVIN:
                    return celsiusValue + 273.15; //  Kelvin
                default:
                    throw new IllegalArgumentException("Unknown temperature unit");
            }
        }

        // we put everything into metres and then use the value for the enum object, aka metre is 1.0
        // but a kilometre would be 1000.00, while a foot is 0.3048 etc
        public static double convertLength(LengthUnits inputUnit, LengthUnits outputUnit, double value){
            double valueInMetres  = value * inputUnit.getConversionFactor();
            return valueInMetres / outputUnit.getConversionFactor();
        }
    }
}