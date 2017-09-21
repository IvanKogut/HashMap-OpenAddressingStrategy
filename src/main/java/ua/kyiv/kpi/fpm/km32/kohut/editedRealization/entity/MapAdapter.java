package ua.kyiv.kpi.fpm.km32.kohut.editedRealization.entity;

public class MapAdapter implements ua.kyiv.kpi.fpm.km32.kohut.entity.Map {

	private java.util.Map<Integer, Long> map;

	public MapAdapter(java.util.Map<Integer, Long> map) {
		this.map = map;
	}

	@Override
    public void put(int key, long value) {
    	map.put(key, value);
    }

    @Override
    public long get(int key) {
    	return map.get(key);
    }

    @Override
    public int size() {
        return map.size();
    }
}