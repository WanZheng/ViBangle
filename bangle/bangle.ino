/*
 * Header of LED
 */
const int COLOR_RED = (1 << 0);
const int COLOR_GREEN = (1 << 1);
const int COLOR_BLUE = (1 << 2);
void led_set_color(int color);

/*
 * Implement of LED
 */
// LED leads connected to PWM pins
const int RED_LED_PIN = 9;
const int GREEN_LED_PIN = 10;
const int BLUE_LED_PIN = 11;

// Used to store the current intensity level of the individual LEDs
int redIntensity = 0;
int greenIntensity = 0;
int blueIntensity = 0;

void led_setup() {
	pinMode(RED_LED_PIN, OUTPUT);
	pinMode(GREEN_LED_PIN, OUTPUT);
	pinMode(BLUE_LED_PIN, OUTPUT);
}

void led_set_color(int color) {
	int redIntensity = 255;
	int greenIntensity = 255;
	int blueIntensity = 255;

	if (color & COLOR_RED) {
		redIntensity = 0;
	}
	if (color & COLOR_GREEN) {
		greenIntensity = 0;
	}
	if (color & COLOR_BLUE) {
		blueIntensity = 0;
	}

        analogWrite(RED_LED_PIN, redIntensity);
        analogWrite(GREEN_LED_PIN, greenIntensity);
        analogWrite(BLUE_LED_PIN, blueIntensity);
}

void setup() {
	led_setup();
}

/*
 * example
 */
void loop() {
	led_set_color(COLOR_RED);
        delay(1000);
	led_set_color(COLOR_GREEN);
        delay(1000);
	led_set_color(COLOR_BLUE);
        delay(1000);
	led_set_color(COLOR_RED | COLOR_GREEN);
        delay(1000);
	led_set_color(COLOR_GREEN | COLOR_BLUE);
        delay(1000);
	led_set_color(COLOR_BLUE | COLOR_RED);
        delay(1000);
}
