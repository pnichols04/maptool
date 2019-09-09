package net.rptools.maptool.client;

import net.rptools.maptool.client.ui.Scale;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

public class Test_ZoneRenderer {

  Zone zone = spy(new Zone());
  ZoneRenderer renderer = spy(new ZoneRenderer(zone));
  ArgumentCaptor<PropertyChangeEvent> eventCaptor = ArgumentCaptor.forClass(PropertyChangeEvent.class);

  @Mock
  PropertyChangeListener listener;

  @BeforeEach
  void init() {
    MockitoAnnotations.initMocks(this);
    renderer.addPropertyChangeListener(listener);
  }

  @Test
  void whenSettingZoneScaleToNull_ThenThrowsException() {
    //noinspection ConstantConditions
    assertThrows(NullPointerException.class, () -> renderer.setZoneScale(null),
      "Setting `zoneScale` to `null` should throw a NullPointerException.");
  }

}
