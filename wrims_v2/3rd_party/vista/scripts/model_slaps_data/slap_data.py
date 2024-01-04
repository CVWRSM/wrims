from hecutils import *
#Slaps Data with Model Estimates. If the deviation is outside project model uncertainity bounds, it reports those time windows
def get_threshold_model(threshold):
    pass
def slap_data(timeseries, model):
    timeseries = model.apply(timeseries)
