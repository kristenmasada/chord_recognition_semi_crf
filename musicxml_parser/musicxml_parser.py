# Kristen Masada
# Converts MusicXML file to XML file with event-based info


import sys
import io
import re
from lxml import builder, etree
from music21 import converter, note, chord, stream, search

# import MusicXML file
file = sys.argv[1]
filename = file[:-4] 
score = converter.parse(file)

# initialize song tag, which will be root tag of xml file
song = etree.Element("song")

# top-level comment providing brief description about units for onset/duration
comment = "Durations are in quarter length format, in which a quarter note is of length 1.0, an eighth note is of length 0.5, etc. Onsets are relative to the beginning of the score, starting at index 0.0, and are in quarter length format as well."
song.addprevious(etree.Comment(comment))

# create title tag from song tag; include song title as text
etree.SubElement(song, "title").text = filename

# create XML for length of song
etree.SubElement(song, "length").text = str(score.quarterLength)

# convert score to TimespanTree object 
scoreTree = score.asTimespans(classList = (note.Note,chord.Chord))

# grab events from scoreTree
chord_events = list(scoreTree.iterateVerticalities())

# get chords from lyrics
reSearchChords = re.compile(r'[^\s]+', re.IGNORECASE)
ls = search.lyrics.LyricSearcher(score)
chords = ls.search(reSearchChords)

# put chords into sorted dictionary, with key as onset time and value as chord name
chords_dict = {}
for c in chords:
  chords_dict[c.els[0].getOffsetInHierarchy(score)] = c.matchText.split("|")[0]   # get preferred chord annotation
chords_keys = sorted(chords_dict.keys())

# create XML for events
events = etree.SubElement(song, "events")
i = 0                                       # counter for index number of event in array
events_dict = {}                            # events dictionary, with key as onset time and value as index number;
                                            # used later for getting eventStart and eventStop info for segments
prev_label = chords_dict[min(chords_dict)]  # chord label of event with smallest key
for e in chord_events:
  event = etree.SubElement(events, "event")
  etree.SubElement(event, "index").text = str(i)
  if e.offset in chords_keys:
    etree.SubElement(event, "tag").text = "B-" + chords_dict[e.offset]
    prev_label = chords_dict[e.offset]
  else:
    etree.SubElement(event, "tag").text = "I-" + prev_label
  etree.SubElement(event, "onset").text = str(e.offset)
  events_dict[e.offset] = i
  if e.nextStartOffset:
    etree.SubElement(event, "duration").text = str(e.nextStartOffset - e.offset)
  else:
    etree.SubElement(event, "duration").text = str(score.quarterLength - e.offset)
  etree.SubElement(event, "measureNumber").text = str(e.measureNumber)
  etree.SubElement(event, "accent").text = str(e.beatStrength)
  notes = etree.SubElement(event, "notes")
  for s in e.startTimespans:
    duration = s.quarterLength
    for p in s.pitches:
      note = etree.SubElement(notes, "note")
      noteNameWithOctave = p.nameWithOctave
      noteNameWithOctave = noteNameWithOctave.replace("-", "b")
      etree.SubElement(note, "pitch").text = noteNameWithOctave
      etree.SubElement(note, "duration").text = str(duration)
      etree.SubElement(note, "fromPrevious").text = "False"
      etree.SubElement(note, "accent").text = str(e.beatStrength)
      etree.SubElement(note, "onset").text = str(e.offset)
  for v in e.overlapTimespans:
    duration = v.quarterLength
    for p in v.pitches:
      note = etree.SubElement(notes, "note")
      noteNameWithOctave = p.nameWithOctave
      noteNameWithOctave = noteNameWithOctave.replace("-", "b")
      etree.SubElement(note, "pitch").text = noteNameWithOctave
      etree.SubElement(note, "duration").text = str(duration)
      etree.SubElement(note, "fromPrevious").text = "True"
      etree.SubElement(note, "accent").text = str(e.beatStrength)
  i += 1

# create XML for segments
segments = etree.SubElement(song, "segments")
for i, key in enumerate(chords_keys):
  segment = etree.SubElement(segments, "segment")
  etree.SubElement(segment, "chordLabel").text = chords_dict[key]
  etree.SubElement(segment, "onset").text = str(key)
  if i < (len(chords) - 1):
    etree.SubElement(segment, "offset").text = str(chords_keys[i + 1])
  else:
    etree.SubElement(segment, "offset").text = str(score.quarterLength)
  etree.SubElement(segment, "eventStart").text = str(events_dict[key])
  if i < (len(chords) - 1):
    etree.SubElement(segment, "eventStop").text = str(events_dict[chords_keys[i + 1]])
  else:
    etree.SubElement(segment, "eventStop").text = str(len(events_dict))

    
# output xml file
xml_filename = filename + "_events.xml"
et = etree.ElementTree(song)
et.write(xml_filename, encoding = "UTF-8", pretty_print = True, xml_declaration = True)

