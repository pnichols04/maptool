package net.rptools.maptool.client.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.mockito.Mockito.*;

public class Test_Scale {

  Scale scale;
  ArgumentCaptor<PropertyChangeEvent> eventCaptor;

  @Mock
  PropertyChangeListener listener;

  @BeforeEach
  void init() {
    scale = spy(new Scale());
    eventCaptor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    MockitoAnnotations.initMocks(this);
    scale.addPropertyChangeListener(listener);
  }

  @Test
  void whenOffsetChanges_ThenFiresEvent() {
    scale.setOffset(17, 19);
    verify(listener, times(1)).propertyChange(eventCaptor.capture());
    Assert.equals(Scale.PROPERTY_OFFSET, eventCaptor.getValue().getPropertyName(),
          "`getPropertyName()` should have returned `Scale.PROPERTY_OFFSET`");
    var expectedOldValue = new Point(0, 0);
    Assert.equals(expectedOldValue, eventCaptor.getValue().getOldValue(),
          "`getOldValue()` should have returned " + expectedOldValue.toString());
    var expectedNewValue = new Point(17, 19);
    Assert.equals(expectedNewValue, eventCaptor.getValue().getNewValue(),
          "`getNewValue()` should have returned " + expectedNewValue.toString());
  }

  @Test
  void whenScaleChanges_ThenFiresEvent() {
    var expectedNewValue = 0.5;
    scale.setScale(expectedNewValue);
    verify(listener, times(1)).propertyChange(eventCaptor.capture());
    Assert.equals(Scale.PROPERTY_SCALE, eventCaptor.getValue().getPropertyName(),
          "getPropertyName() should have returned `Scale.PROPERTY_SCALE`");
    var expectedOldValue = 1.0;
    Assert.equals(expectedOldValue, eventCaptor.getValue().getOldValue(),
          "`getOldValue()` should have returned " + expectedOldValue);
    Assert.equals(expectedNewValue, eventCaptor.getValue().getNewValue(),
          "`getNewValue()` should have returned " + expectedNewValue);
    Assert.equals(expectedNewValue, scale.getScale(),
          "`getScale()` should have returned the new value`");
  }

  @Test
  void whenScaleSetToSameValue_FiresNoEvent() {
    var oldValue = scale.getScale();
    scale.setScale(oldValue);
    verify(listener, never()).propertyChange(any());
  }

  @Test
  void whenScaleUp_ThenFiresEvent() {
    scale.scaleUp();
    verify(listener, times(1)).propertyChange(eventCaptor.capture());
    Assert.equals(Scale.PROPERTY_SCALE, eventCaptor.getValue().getPropertyName(),
          "getPropertyName() should have returned `Scale.PROPERTY_SCALE`");
    var expectedOldValue = 1.0;
    Assert.equals(expectedOldValue, eventCaptor.getValue().getOldValue(),
          "`getOldValue()` should have returned " + expectedOldValue);
    var expectedNewValue = scale.getScale();
    Assert.equals(expectedNewValue, eventCaptor.getValue().getNewValue(),
          "`getNewValue() should have returned " + expectedNewValue);
  }

  @Test
  void whenScaleDown_ThenFiresEvent() {
    scale.scaleDown();
    verify(listener, times(1)).propertyChange(eventCaptor.capture());
    Assert.equals(Scale.PROPERTY_SCALE, eventCaptor.getValue().getPropertyName(),
          "getPropertyName() should have returned `Scale.PROPERTY_SCALE`");
    var expectedOldValue = 1.0;
    Assert.equals(expectedOldValue, eventCaptor.getValue().getOldValue(),
          "`getOldValue()` should have returned " + expectedOldValue);
    var expectedNewValue = scale.getScale();
    Assert.equals(expectedNewValue, eventCaptor.getValue().getNewValue(),
          "`getNewValue() should have returned " + expectedNewValue);
  }

  @Test
  void whenScaleResetOnNewObject_ThenFiresNoEvent() {
    var initialValue = scale.getScale();
    scale.reset();
    var newValue = scale.getScale();
    verify(listener, never()).propertyChange(any());
    Assert.equals(initialValue, newValue,
          "Calling `reset()` on a new instance should not have changed the scale.");
  }

  @Test
  void whenScaleUpAfterScaleDown_ThenInitialScaleResumed() {
    var initialValue  = scale.getScale();
    scale.scaleDown();
    scale.scaleUp();
    Assert.equals(initialValue, scale.getScale(),
          "After `scaleDown()` then `scaleUp()`, the scale should be back to the initial value.");
  }

  @Test
  void whenScaleDownAfterScaleUp_ThenInitialScaleResumed() {
    var initialValue  = scale.getScale();
    scale.scaleUp();
    scale.scaleDown();
    Assert.equals(initialValue, scale.getScale(),
          "After `scaleDown()` then `scaleUp()`, the scale should be back to the initial value.");
  }

  @Test
  void whenOffsetSetWithNoChange_ThenFiresNoEvent() {
    var x = scale.getOffsetX();
    var y = scale.getOffsetY();
    scale.setOffset(x, y);
    verify(listener, never()).propertyChange(any());
  }

  @Test
  void whenCallingZoomReset_OneEventFires() {
    scale.scaleUp();
    clearInvocations(listener);
    scale.zoomReset(17, 19);
    verify(listener, times(1)).propertyChange(any());
  }

  @Test
  void whenCallingZoomIn_OneEventFires() {
    scale.scaleUp();
    clearInvocations(listener);
    scale.zoomIn(17, 19);
    verify(listener, times(1)).propertyChange(any());
  }

  @Test
  void whenCallingZoomOut_OneEventFires() {
    scale.scaleUp();
    clearInvocations(listener);
    scale.zoomOut(17, 19);
    verify(listener, times(1)).propertyChange(any());
  }

  @Test
  void whenCallingZoomScale_OneEventFires() {
    scale.scaleUp();
    clearInvocations(listener);
    scale.zoomScale(17, 19, 1.5);
    verify(listener, times(1)).propertyChange(any());
  }


}
