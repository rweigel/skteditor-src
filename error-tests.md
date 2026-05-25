# skteditor-1.3.11 Error Tests

All validation checks that produce error or warning messages, with links to source lines.

Generated using Claude Sonnet 4.6 High on 2026-05-25.

Base URL: https://github.com/rweigel/skteditor-src/tree/v1.3.11

---

## gsfc/spdf/istp/ISTPCompliance.java

| Line | Message |
|------|---------|
| [382](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L382) | Missing a required "ISTP epoch" variable. |
| [390](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L390) | `<epoch>` has compression option set to `<name>`. ISTP Epoch variables should not be compressed. |
| [400](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L400) | ISTP epoch variable `<name>` is missing the UNITS attribute. |
| [407](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L407) | The UNITS attribute has been added. |
| [417](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L417) | ISTP epoch variable `<name>` is missing the TIME_BASE attribute. |
| [424](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L424) | The TIME_BASE attribute has been added. |
| [625](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L625) | Global attribute `<name>` contains non-ASCII characters in entry `<entry>`. |
| [647](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L647) | Global attribute `<name>` is of type `<type>`. |
| [1282](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1282) | Dimension error: has dimension `<n>` and it should be 0. |
| [1296](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1296) | VAR_TYPE value `<value>` is not valid. Must be one of: `data`, `support_data`, `metadata`, `ignore_data`. |
| [1400](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1400) | data value `<value>` is outside the range `[VALIDMIN, VALIDMAX]` defined in metadata. |
| [1411](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1411) | VAR_TYPE is missing. |
| [1442](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1442) | FIELDNAM is missing. |
| [1448](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1448) | CATDESC is missing. |
| [1511](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1511) | `<var>` has a dimension of `<N>` but the sizes of the dimensions is missing. |
| [1550](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1550) | `<var>`'s `<N>`th dimension has a size of `<X>`. Sizes < 2 are likely to be a problem. |
| [1826](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1826) | Variable has both a UNITS and UNIT_PTR attributes. |
| [1850](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L1850) | UNIT_PTR value does not point to an existing variable. |
| [2321](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L2321) | `<dependName>` has dimension 0. It should have a dimension of `<N>` as declared in DEPEND_`<N>`. |
| [2327](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L2327) | `<dependName>` has dimension `<N>` and it should be `<M>` as declared in DEPEND_`<N>`. |
| [2338](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L2338) | `<dependName>` is wrong size (`<N>` when it should be `<M>`). |
| [2390](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L2390) | `<dependName>` attribute is missing. |
| [2830](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L2830) | FORMAT syntax error: `<message>`. The FORMAT attribute must be a valid Fortran or C format specification (e.g., `F10.3`, `I5`, `%10.3f`). |
| [3040](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3040) | `<format>` contains an illegal character. |
| [3048](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3048) | FORMAT `<format>` specification has a width value that exceeds maximum. |
| [3235](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3235) | DISPLAY_TYPE attribute value `<value>` is invalid. Valid types: `time_series`, `spectrogram`, `stack_plot`, `orbit`, `image`, `movie`, `no_plot`, `bar_chart`, `flux_image`, `flux_movie`, `fuv_image`, `fuv_movie`, `label`, `line`, `list`, `time_text`, `wind_movie`, `wind_plot`, `xy_plot`, `plasma_movie`, `plasmagram`, `topside_ionogram`, `radar_vector`, `map_image`, `map_movie`, `mapped`, `skymap`, `skymap_movie`, `default_dimension`. |
| [3301](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3301) | DISPLAY_TYPE error: `<message>`. |
| [3501](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3501) | Virtual variable FUNCTION attribute is null (missing or empty). |
| [3517](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3517) | Virtual variable FUNCTION `<function>` is not recognized. Valid functions: `add_51s`, `alternate_view`, `apply_qflag`, `calc_p`, `comp_themis_epoch`, `comp_themis_epoch16`, `compute_magnitude`, `conv_pos`, `conv_pos1`, `conv_pos2`, `convert_log10`, `create_plain_vis`, `create_plmap_vis`, `create_vis`, `crop_image`, `flip_image`, `height_isis`, `region_filt`, `wind_plot`. |
| [3531](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3531) | COMPONENT_`<N>` attribute is missing. |
| [3538](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3538) | COMPONENT_`<N>` value of `<value>` does not point to an existing variable. |
| [3552](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L3552) | Virtual variable component dimension `<N>` does not match the required dimension `<M>` for the given FUNCTION. |
| [4147](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4147) | VALIDMIN >= VALIDMAX. |
| [4382](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4382) | Project has no entries. |
| [4384](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4384) | Project global attribute is missing. |
| [4390](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4390) | Source_name has no entries. |
| [4392](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4392) | Source_name global attribute is missing. |
| [4398](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4398) | Descriptor has no entries. |
| [4400](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4400) | Descriptor global attribute is missing. |
| [4406](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4406) | Data_type has no entries. |
| [4408](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4408) | Data_type global attribute is missing. |
| [4414](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4414) | PI_name has no entries. |
| [4416](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4416) | PI_name global attribute is missing. |
| [4422](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4422) | PI_affiliation has no entries. |
| [4424](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4424) | PI_affiliation global attribute is missing. |
| [4430](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4430) | TEXT has no entries. |
| [4432](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4432) | TEXT global attribute is missing. |
| [4438](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4438) | Discipline has no entries. |
| [4440](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4440) | Discipline global attribute is missing. |
| [4449](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4449) | Mission_group has no entries. |
| [4454](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4454) | Mission_group global attribute is missing. |
| [4463](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4463) | Instrument_type has no entries. |
| [4468](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4468) | Instrument_type global attribute is missing. |
| [4477](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4477) | Logical_source_description has no entries. |
| [4482](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4482) | Logical_source_description global attribute is missing. |
| [4490](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4490) | Logical_file_id has no entries. |
| [4492](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4492) | Logical_file_id global attribute is missing. |
| [4606](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4606) | spase_DatasetResourceID is missing. |
| [4669](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4669) | LINK_TITLE `<N>` value does not have a corresponding HTTP_LINK `<N>` value. |
| [4674](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4674) | HTTP_LINK `<N>` value does not have a corresponding LINK_TITLE `<N>` value. |
| [4684](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4684) | HTTP_LINK `<N>` value does not begin with 'http://' or 'https://'. |
| [4716](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/ISTPCompliance.java#L4716) | Malformed HTTP Link URL `<N>`: `<message>`. |

---

## gsfc/spdf/istp/Variable.java

| Line | Message |
|------|---------|
| [265](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/Variable.java#L265) | New variable name `<name>` conflicts with existing variable `<existing>` — the names differ only in case. CDF variable names must be case-insensitively distinct. |
| [290](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/Variable.java#L290) | Renamed variable `<name>` conflicts with existing variable `<existing>` — names differ only in case. |
| [323](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/Variable.java#L323) | Copied variable `<name>` conflicts with existing variable `<existing>` — names differ only in case. |

---

## gsfc/spdf/istp/GlobalAttribute.java

| Line | Message |
|------|---------|
| [365](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/GlobalAttribute.java#L365) | New global attribute name `<name>` conflicts with existing attribute `<existing>` — the names differ only in case. CDF attribute names must be case-insensitively distinct. |

---

## gsfc/spdf/istp/Epoch.java

| Line | Message |
|------|---------|
| [99](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/Epoch.java#L99) | Variable `<name>` is not an ISTP epoch variable. An ISTP epoch variable must have data type `CDF_EPOCH`, `CDF_EPOCH16`, `CDF_TIME_TT2000`, or `CDF_TIME_EPOCH8`, and VAR_TYPE of `support_data`. |

---

## gsfc/spdf/istp/AxisDefinition.java

| Line | Message |
|------|---------|
| [71](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/AxisDefinition.java#L71) | DISPLAY_TYPE axis identifier `<axis>` is not valid. Must be a single character: `x`, `y`, or `z`. |
| [344](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/AxisDefinition.java#L344) | DISPLAY_TYPE axis definition syntax is invalid in `<value>`. Expected format: `x=varName`, `y=varName(index)`, or `z=varName(row,col)`. |

---

## gsfc/spdf/istp/DisplayType.java

| Line | Message |
|------|---------|
| [121](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L121) | DISPLAY_TYPE string is empty — the type portion (before the first `>`) is missing. |
| [125](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L125) | DISPLAY_TYPE value `<type>` is not one of the recognized type names (e.g., `time_series`, `spectrogram`, `orbit`, `image`, `stack_plot`, etc.). |
| [140](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L140) | DISPLAY_TYPE keyword `<keyword>` (the part after `>` and before the axis definitions) is not valid for the given display type. For example, `stack_plot` accepts `nobar`; `plasmagram` accepts `thumbsize`, `symsize`, `labl`. |
| [149](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L149) | DISPLAY_TYPE axis definition is incomplete — an opening `(` was found without a closing `)` and no comma-separated continuation was available. |
| [162](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L162) | DISPLAY_TYPE axis definition is incomplete — same as L149, triggered on a subsequent axis token. |
| [174](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L174) | DISPLAY_TYPE is missing an axis definition after the `>` separator — a `>` was present but no `x=`, `y=`, or `z=` followed. |
| [181](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/DisplayType.java#L181) | DISPLAY_TYPE trailing token found after parsing all axis definitions — extra unparsed content remains. |

---

## gsfc/spdf/istp/AbstractDisplayTypeParameter.java

| Line | Message |
|------|---------|
| [76](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/AbstractDisplayTypeParameter.java#L76) | DISPLAY_TYPE parameter string is empty after the `>` separator. Expected an axis assignment (`x=`, `y=`, `z=`), a coordinate system (`coord=gci`), or a plain value. |
| [90](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/AbstractDisplayTypeParameter.java#L90) | DISPLAY_TYPE parameter `<value>` is invalid. The left-hand side of `=` must be `x`, `y`, `z`, or `coord`. |

---

## gsfc/spdf/istp/CoordinateSystem.java

| Line | Message |
|------|---------|
| [48](https://github.com/rweigel/skteditor-src/blob/v1.3.11/skteditor-1.3.11/src/gsfc/spdf/istp/CoordinateSystem.java#L48) | DISPLAY_TYPE `coord=<name>` value is not a recognized coordinate system. Valid values: `gci`, `gse`, `gsm`, `tod`, `j2000`, `geo`, `gm`, `sm`, `hec`. |
