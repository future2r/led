package name.ulbricht.led;

public enum TutorialCategory {

    BASICS, ADVANCED, DEMO;

    public String getDisplayName() {
        return Resources.getString(String.format("TutorialCategory.%s.displayName", name()));
    }

}
