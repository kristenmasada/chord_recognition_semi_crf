package cr;

/**
 * Interface to FeatureType in FeatureManager<br>
 * Each feature can be disabled/enabled
 * @author Aldrian Obaja <aldrianobaja.m@gmail.com>
 *
 */	

public interface IFeatureType {
	public void enable();
	public void disable();
	public boolean enabled();
}
