/**
 *  Dome On Off Plug v0.0.0
 *  (Model: DMOF1)
 *
 *  Author: 
 *    Kevin LaFramboise (krlaframboise)
 *
 *  URL to documentation:  
 *    
 *
 *  Changelog:
 *
 *    0.0.0 (01/29/2017)
 *      - Initial Release
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (
		name: "Dome On Off Plug", 
		namespace: "krlaframboise", 
		author: "Kevin LaFramboise"
	) {
		capability "Actuator"
		capability "Sensor"
		capability "Switch"
		capability "Outlet"
		capability "Power Meter"
		capability "Energy Meter"
		capability "Voltage Measurement"
		capability "Configuration"
		capability "Refresh"
		capability "Polling"
		
		attribute "lastCheckin", "number"
		attribute "status", "string"
		attribute "current", "number"
		attribute "currentL", "number"
		attribute "currentH", "number"
		attribute "voltageL", "number"
		attribute "voltageH", "number"
		attribute "powerL", "number"
		attribute "powerH", "number"
		attribute "energyCost", "number"
		attribute "energySince", "string"
		
		command "resetEnergy"
		command "resetPower"
		command "resetVoltage"
		command "resetCurrent"
		
		fingerprint deviceId: "0x1001", inClusters: "0x20, 0x25, 0x27, 0x32, 0x59, 0x5A, 0x5E, 0x70, 0x71, 0x72, 0x73, 0x85, 0x86"
		
		fingerprint mfr:"021F", prod:"0003", model:"0087"
	}
	
	simulator { }
	
	preferences {
		input "ledEnabled", "enum",
			title: "Enable/Disable LED:",
			defaultValue: ledEnabledSetting,
			required: false,
			displayDuringSetup: true,
			options: ledEnabledOptions.collect { it.name }
		input "buttonEnabled", "enum",
			title: "Enable/Disable Physical Button:",
			defaultValue: buttonEnabledSetting,
			required: false,
			displayDuringSetup: true,
			options: buttonEnabledOptions.collect { it.name }
		input "timerInterval", "enum",
			title: "Auto-Off Interval:",
			defaultValue: timerIntervalSetting,
			required: false,
			displayDuringSetup: true,
			options: timerIntervalOptions.collect { it.name }
		input "memoryEnabled", "enum",
			title: "Enable/Disable Remember On/Off:",
			defaultValue: memoryEnabledSetting,
			required: false,
			displayDuringSetup: true,
			options: memoryEnabledOptions.collect { it.name }
		input "meterInterval", "enum",
			title: "Meter Reporting Interval:",
			defaultValue: meterIntervalSetting,
			required: false,
			displayDuringSetup: true,
			options: meterIntervalOptions.collect { it.name }
		input "meterThreshold", "enum",
			title: "Meter Reporting Threshold (change in amps):",
			defaultValue: meterThresholdSetting,
			required: false,
			displayDuringSetup: true,
			options: meterThresholdOptions.collect { it.name }
		input "overloadOff", "enum",
			title: "Overload Shut-Off Threshold:",
			defaultValue: overloadOffSetting,
			required: false,
			displayDuringSetup: true,
			options: overloadOffOptions.collect { it.name }
		input "overloadWarning", "enum",
			title: "Overload Warning LED Threshold:",
			defaultValue: overloadWarningSetting,
			required: false,
			displayDuringSetup: true,
			options: overloadWarningOptions.collect { it.name }
		input "energyPrice", "number",
			title: "\$/kWH Cost:",
			defaultValue: energyPriceSetting,
			required: false,
			displayDuringSetup: true
		input "debugOutput", "bool", 
			title: "Enable debug logging?", 
			defaultValue: true, 
			required: false
	}

	tiles(scale: 2) {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: 'on', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		standardTile("refresh", "device.refresh", width: 2, height: 2, decoration: "flat") {
			state "refresh", label:'Refresh All', action: "refresh", icon:"st.secondary.refresh-icon", defaultState: true
		}
    valueTile("status", "device.status", width: 2, height: 2, decoration: "flat") {
			state "default", label:'Energy Reset:\n${currentValue}', defaultState: true
      state "overload", label:'Overload Detected'
    }
		standardTile("resetEnergy", "general", width: 2, height: 2, decoration: "flat") {
			state "default", label:'Reset Energy', action: "resetEnergy", icon:"st.secondary.refresh-icon", defaultState: true
		}
		valueTile("energy", "device.energy", width: 2, height: 2) {
			state "val", label:'${currentValue} kWh', unit: "kWh", defaultState: true, backgroundColor: "#CCCCCC"
		}
		valueTile("energyCost", "device.energyCost", width: 2, height: 2) {
			state "val", label:'\$${currentValue}', unit: "", defaultState: true, backgroundColor: "#CCCCCC"
		}
		standardTile("resetPower", "general", width: 2, height: 2, decoration: "flat") {
			state "default", label:'Reset Power', action: "resetPower", icon:"st.secondary.refresh-icon", defaultState: true
		}
		valueTile("power", "device.power", width: 2, height: 2) {
			state "val", label:'${currentValue} W', unit: "W", defaultState: true, backgroundColor: "#CCCCCC"
		}
		valueTile("powerL", "device.powerL", width: 2, height: 1) {
			state "val", label:'L: ${currentValue} W', unit: "W", defaultState: true
		}
		valueTile("powerH", "device.powerH", width: 2, height: 1) {
			state "val", label:'H: ${currentValue} W', unit: "W", defaultState: true
		}
		standardTile("resetVoltage", "general", width: 2, height: 2, decoration: "flat") {
			state "default", label:'Reset Voltage', action: "resetVoltage", icon:"st.secondary.refresh-icon", defaultState: true
		}
		valueTile("voltage", "device.voltage", width: 2, height: 2) {
			state "val", label:'${currentValue} V', unit: "V", defaultState: true, backgroundColor: "#CCCCCC"
		}
		valueTile("voltageL", "device.voltageL", width: 2, height: 1) {
			state "val", label:'L: ${currentValue} V', unit: "V", defaultState: true
		}
		valueTile("voltageH", "device.voltageH", width: 2, height: 1) {
			state "val", label:'H: ${currentValue} V', unit: "V", defaultState: true
		}
		standardTile("resetCurrent", "general", width: 2, height: 2, decoration: "flat") {
			state "default", label:'Reset Current', action: "resetCurrent", icon:"st.secondary.refresh-icon", defaultState: true
		}
		valueTile("current", "device.current", width: 2, height: 2) {
			state "val", label:'${currentValue} A', unit: "A", defaultState: true, backgroundColor: "#CCCCCC"
		}
		valueTile("currentL", "device.currentL", width: 2, height: 1) {
			state "val", label:'L: ${currentValue} A', unit: "A", defaultState: true
		}
		valueTile("currentH", "device.currentH", width: 2, height: 1) {
			state "val", label:'H: ${currentValue} A', unit: "A", defaultState: true
		}
		main "switch"
		details(["switch", "refresh", "status", "resetEnergy", "energy", "energyCost", "resetTotal", "resetPower", "power", "powerL", "powerH", "resetVoltage", "voltage", "voltageL", "voltageH", "resetCurrent", "current", "currentL", "currentH"])
	}
}

def updated() {	
	// This method always gets called twice when preferences are saved.
	if (!isDuplicateCommand(state.lastUpdated, 3000)) {		
		state.lastUpdated = new Date().time
		logTrace "updated()"
		return response(configure())		
	}		
}

def configure() {
	logTrace "configure()"
	def cmds = []
	def refreshAll = (!state.isConfigured || !settings?.ledEnabled)
	
	if (!state.isConfigured) {
		logTrace "Waiting 1 second because this is the first time being configured"		
		updateEnergySince()
		cmds << "delay 1000"
		cmds << meterResetCmd()
	}
	
	configData.sort { it.paramNum }.each { 
		cmds += updateConfigVal(it.paramNum, it.value, it.size, refreshAll)	
	}
	
	if (cmds) {
		logDebug "Sending configuration to device."
		return delayBetween(cmds, 1000)
	}
	else {
		return cmds
	}	
}

private updateConfigVal(paramNum, val, size, refreshAll) {
	def result = []
	def configVal = state["configVal${paramNum}"]
	
	if (refreshAll || (configVal != val)) {
		result << configSetCmd(paramNum, val, size)
		result << configGetCmd(paramNum)
	}	
	return result
}

def poll() {
	logTrace "poll()"
	if (canCheckin()) {		
		return refresh()
	}
	else {
		logDebug "Ignored poll request because it hasn't been long enough since the last poll."
	}
}

def refresh() {	
	logTrace "refresh()"
	def result = []
	result << switchBinaryGetCmd()
	(0..5).each {
		result << meterGetCmd(it)
	}	
	return delayBetween(result, 1000)	
}

def on() {
	logTrace "on()"
	return [
		switchBinarySetCmd(0xFF),
		switchBinaryGetCmd()
	]
}

def off() {
	logTrace "off()"
	return [
		switchBinarySetCmd(0x00),
		switchBinaryGetCmd()
	]
}

def resetEnergy() {
	logDebug "Resetting Energy"
	updateEnergySince()
	return delayBetween([meterResetCmd(), meterGetCmd(0)], 1000)
}

private updateEnergySince() {
	def val = convertToLocalTimeString(new Date())
	sendEvent(getEventMap("energySince", val, false))
	sendEvent(getEventMap("status", val, true, "Energy Reset"))
}

def resetPower() {
	resetMeterHistory("power", "W")
}

def resetVoltage() {
	resetMeterHistory("voltage", "V")
}

def resetCurrent() {
	resetMeterHistory("current", "A")
}

private resetMeterHistory(name, unit) {
	logDebug "Resetting ${name.capitalize()}"
	def val = device.currentValue("$name")
	sendEvent(getEventMap("${name}L", val, false, null, unit))
	sendEvent(getEventMap("${name}H", val, false, null, unit))
}

def parse(String description) {
	def result = []

	def cmd = zwave.parse(description, commandClassVersions)
	if (cmd) {
		result += zwaveEvent(cmd)
	}
	else {
		logDebug "Unable to parse description: $description"
	}
	
	if (canCheckin()) {
		result << createEvent(name: "lastCheckin",value: new Date().time, isStateChange: true, displayed: false)
	}
	
	return result
}

private getCommandClassVersions() {
	[
		0x20: 1,	// Basic
		0x25: 1,	// Binary Switch
		0x27: 1,	// All Switch
		0x32: 3,	// Meter v4
		0x59: 1,  // AssociationGrpInfo
		0x5A: 1,  // DeviceResetLocally
		0x5E: 2,  // ZwaveplusInfo
		0x70: 1,  // Configuration
		0x71: 3,  // Notification v4
		0x72: 2,  // ManufacturerSpecific
		0x73: 1,  // Powerlevel
		0x85: 2,  // Association
		0x86: 1		// Version (2)
	]
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {	
	def name = configData.find { it.paramNum == cmd.parameterNumber }?.name
	if (name) {	
		def val = hexToInt(cmd.configurationValue, cmd.size)
	
		logDebug "${name} = ${val}"
	
		state."configVal${cmd.parameterNumber}" = val
	}
	else {
		logDebug "Parameter ${cmd.parameterNumber}: ${cmd.configurationValue}"
	}
	state.isConfigured = true	
	return []
}

private hexToInt(hex, size) {
	if (size == 2) {
		return hex[1] + (hex[0] * 0x100)
	}
	else {
		return hex[0]
	}
}

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
	def result = []	
	logTrace "NotificationReport: $cmd"
	
	if (cmd.notificationType == 0x08) {
		switch (cmd.event) {
			case 0x08:
				logDebug "Overload Detected"
				result << createEvent(getEventMap("status", "overload"))
				break
			case 0x00:
				logDebug "Overload Clear"
				result << createEvent(getEventMap("status", device.currentValue("energySince")))
				break
		}
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	def result = []	
	def name
	def unit 
	def val = cmd.scaledMeterValue
	
	switch (cmd.scale) {
		case 0:
			name = "energy"
			unit = "kWh"
			break
		case 2:
			name = "power"
			unit = "W"
			break
		case 4:
			name = "voltage"
			unit = "V"
			break
		case 5:
			name = "current"
			unit = "A"
			break
		default:
			logTrace "unknown scale ${cmd.scale}: ${val}"
	}
	
	if (name) {
		if (name == "energy"){
			result += createEnergyCostEvents(val)
		}
		else {
			result += createMeterHistoryEvents(name, val, unit, true)
			result += createMeterHistoryEvents(name, val, unit, false)
		}
		if (device.currentValue("$name") != val) {
			result << createEvent(getEventMap(name, val, null, "${name} is ${val} ${unit}", unit))
		}
	}
	return result
}

private createMeterHistoryEvents(mainName, mainVal, unit, lowEvent) {
	def name = "${mainName}${lowEvent ? 'L' : 'H'}"
	def val = safeToDec(device.currentValue("${name}"), mainVal)
	
	def result = []
	if ((lowEvent && (mainVal <= val)) || (!lowEvent && (mainVal >= val))) {
		result << createEvent(getEventMap(name, mainVal, false, "", unit))
	}
	return result
}

private createEnergyCostEvents(energyVal) {
	def val = safeToDec(energyVal, 0) * energyPriceSetting
	val = Math.round(val * 100) / 100 // Round to 2 places
	
	def result = []
	if (device.currentValue("energyCost") != val) {
		result << createEvent(getEventMap("energyCost", val, false, ""))
	}	
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	def result = []
	logTrace "BasicReport: ${cmd}"
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	def result = []
	logTrace "SwitchBinaryReport: ${cmd}"
	def val = (cmd.value == 0xFF) ? "on" : "off"
		
	result << createEvent(getEventMap("switch", val))
	return result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	logDebug "Unhandled Command: $cmd"
	return []
}

private getEventMap(name, value, displayed=null, desc=null, unit=null) {	
	def isStateChange = (device.currentValue(name) != value)
	displayed = (displayed == null ? isStateChange : displayed)
	def eventMap = [
		name: name,
		value: value,
		displayed: displayed,
		isStateChange: isStateChange
	]
	if (desc) {
		eventMap.descriptionText = desc
	}
	if (unit) {
		eventMap.unit = unit
	}
	if (isStateChange) {
		logDebug "${name} is ${value} ${unit ?: ''}"
	}
	else {
		logTrace "Creating Event: ${eventMap}"
	}
	return eventMap
}


// Z-Wave Commands
private meterGetCmd(scale) {
	return zwave.meterV3.meterGet(scale: scale).format()
}

private meterResetCmd() {
	return zwave.meterV3.meterReset().format()
}

private switchBinaryGetCmd() {
	return zwave.switchBinaryV1.switchBinaryGet().format()
}

private switchBinarySetCmd(val) {
	return zwave.switchBinaryV1.switchBinarySet(switchValue: val).format()
}

private configGetCmd(paramNum) {
	return zwave.configurationV1.configurationGet(parameterNumber: paramNum).format()
}

private configSetCmd(paramNum, val, size) {
	return zwave.configurationV1.configurationSet(parameterNumber: paramNum, size: size, scaledConfigurationValue: val).format()
}


// Configuration Parameters
private getConfigData() {
	return [
		[paramNum: 1, name: "Meter Reporting Enabled", value: ((meterIntervalSetting?.contains("Disabled")) ? 0 : 1), size: 1],
		[paramNum: 2, name: "Meter Reporting Interval", value: convertOptionSettingToInt(meterIntervalOptions, meterIntervalSetting), size: 2],		
		[paramNum: 3, name: "Overload Shut-Off Threshold", value: getOverloadOffSettingVal(), size: 1],
		[paramNum: 4, name: "Overload Warning Threshold", value: getOverloadWarningSettingVal(), size: 1],
		[paramNum: 5, name: "Enable/Disable LED", value: convertOptionSettingToInt(ledEnabledOptions, ledEnabledSetting), size: 1],	
		[paramNum: 6, name: "Meter Reporting Threshold (amps)", value: convertOptionSettingToInt(meterThresholdOptions, meterThresholdSetting), size: 1],
		[paramNum: 7, name: "Enable/Disable Memory", value: convertOptionSettingToInt(memoryEnabledOptions, memoryEnabledSetting), size: 1],
		[paramNum: 8, name: "Auto-Off Timer Enabled", value: ((timerIntervalSetting?.contains("Disabled")) ? 0 : 1), size: 1],
		[paramNum: 9, name: "Auto-Off Timer Interval:", value: convertOptionSettingToInt(timerIntervalOptions, timerIntervalSetting), size: 2],
		[paramNum: 10, name: "Enable/Disable Physical Button", value: convertOptionSettingToInt(buttonEnabledOptions, buttonEnabledSetting), size: 1]
	]
}

private getOverloadWarningSettingVal() {
	// Overload warning needs to be at least 1 below Overload Off Setting
	def offVal = getOverloadOffSettingVal()
	def val = convertOptionSettingToInt(overloadWarningOptions, overloadWarningSetting)
	return (val >= offVal) ? (offVal - 1) : val
}

private getOverloadOffSettingVal() {
	return convertOptionSettingToInt(overloadOffOptions, overloadOffSetting)
}


// Settings
private getEnergyPriceSetting() {
	return safeToDec(settings?.energyPrice, 0.12)
}
private getLedEnabledSetting() {
	return settings?.ledEnabled ?: findDefaultOptionName(ledEnabledOptions)
}

private getButtonEnabledSetting() {
	return settings?.buttonEnabled ?: findDefaultOptionName(buttonEnabledOptions)
}

private getMemoryEnabledSetting() {
	return settings?.memoryEnabled ?: findDefaultOptionName(memoryEnabledOptions)
}

private getMeterIntervalSetting() {
	 return settings?.meterInterval	?: findDefaultOptionName(meterIntervalOptions)
}

private getMeterThresholdSetting() {
	 return settings?.meterThreshold	?: findDefaultOptionName(meterThresholdOptions)
}

private getTimerIntervalSetting() {
	 return settings?.timerInterval	?: findDefaultOptionName(timerIntervalOptions)
}

private getOverloadOffSetting() {
	return settings?.overloadOff ?: findDefaultOptionName(overloadOffOptions)
}

private getOverloadWarningSetting() {
	return settings?.overloadWarning ?: findDefaultOptionName(overloadWarningOptions)
}


// Setting Options
private getLedEnabledOptions() {
	return getEnabledOptions(1)
}

private getButtonEnabledOptions() {
	return getEnabledOptions(1)
}

private getMemoryEnabledOptions() {
	return getEnabledOptions(1)
}

private getEnabledOptions(defaultVal) {
	[
		[name: "Disabled", value: 0],
		[name: "Enabled", value: 1]
	].each {
		if (it.value == defaultVal) {
			it.name = formatDefaultOptionName("${it.name}")
		}
	}
}

private getOverloadOffOptions() {
	return getAmpOptions(2, 16, 13)
}

private getOverloadWarningOptions() {
	return getAmpOptions(1, 15, 12)
}

private getAmpOptions(min, max, defaultVal) {
	def options = []	
	(min..max).each {
		def val = [name: "${it} Amps", value: it]
		if (it == defaultVal) {
			val.name = formatDefaultOptionName(val.name)
		}		
		options << val
	}	
	return options
}

private getMeterThresholdOptions() {
	return [
		[name: "1%", value: 1],
		[name: formatDefaultOptionName("5%"), value: 5],		
		[name: "10%", value: 10],
		[name: "15%", value: 15],
		[name: "25%", value: 25],
		[name: "50%", value: 50],
		[name: "75%", value: 75],
		[name: "100%", value: 100]
	]	
}

private getMeterIntervalOptions() {
	return [
		[name: "Disabled", value: 300],
		[name: "1 Minute", value: 60],
		[name: "2 Minutes", value: 120],
		[name: formatDefaultOptionName("5 Minutes"), value: 300],
		[name: "10 Minutes", value: 600],
		[name: "30 Minutes", value: 1800],
		[name: "1 Hour", value: 3600],
		[name: "2 Hours", value: 7200],
		[name: "4 Hours", value: 1440],
		[name: "8 Hours", value: 28800]
	]	
}

private getTimerIntervalOptions() {
	return [
		[name: formatDefaultOptionName("Disabled"), value: 1],
		[name: "1 Minute", value: 1],
		[name: "2 Minutes", value: 2],
		[name: "5 Minutes", value: 5],		
		[name: "15 Minutes", value: 10],
		[name: "30 Minutes", value: 30],
		[name: "1 Hour", value: 60],
		[name: "2 Hours", value: 120],
		[name: "4 Hours", value: 240],
		[name: "8 Hours", value: 480],
		[name: "12 Hours", value: 720],
		[name: "1 Day", value: 1440],
		[name: "3 Days", value: 4320],
		[name: "1 Week", value: 10080],
		[name: "2 Weeks", value: 20160],
		[name: "3 Weeks", value: 30240]		
	]	
}

private convertOptionSettingToInt(options, settingVal) {
	return safeToInt(options?.find { "${settingVal}" == it.name }?.value, 0)
}

private formatDefaultOptionName(val) {
	return "${val}${defaultOptionSuffix}"
}

private findDefaultOptionName(options) {
	def option = options?.find { it.name?.contains("${defaultOptionSuffix}") }
	return option?.name ?: ""
}

private getDefaultOptionSuffix() {
	return "   (Default)"
}

private safeToInt(val, defaultVal=-1) {
	return "${val}"?.isInteger() ? "${val}".toInteger() : defaultVal
}

private safeToDec(val, defaultVal=-1) {
	return "${val}"?.isBigDecimal() ? "${val}".toBigDecimal() : defaultVal
}

private convertToLocalTimeString(dt) {
	return dt.format("MM/dd/yyyy hh:mm:ss a", TimeZone.getTimeZone(location.timeZone.ID))
}

private canCheckin() {
	// Only allow the event to be created once per minute.
	def lastCheckin = device.currentValue("lastCheckin")
	return (!lastCheckin || lastCheckin < (new Date().time - 60000))
}

private isDuplicateCommand(lastExecuted, allowedMil) {
	!lastExecuted ? false : (lastExecuted + allowedMil > new Date().time) 
}

private logDebug(msg) {
	if (settings?.debugOutput || settings?.debugOutput == null) {
		log.debug "$msg"
	}
}

private logTrace(msg) {
	// log.trace "$msg"
}