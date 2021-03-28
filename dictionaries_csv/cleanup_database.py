import csv
from re import search

in_filename = 'dutWordList.csv'
out_filename = 'dutWordList_cleaned.csv'

with open(in_filename, encoding="utf8") as csv_in:
    with open(out_filename, "w",  encoding="utf8", newline='') as csv_out:
        reader = csv.reader(csv_in, delimiter=",")
        writer = csv.writer(csv_out, delimiter=",")
        for row in reader:
            # Discard the third row containing unnecessary original language explanations
            row = row[:2]
            # Split the translations; there can be multiple in a single column
            translations = row[1].split(';')
            explanations = []
            for i, x in enumerate(translations):
                # Match the text in brackets, as well as the brackets and the spaces around them
                if (expl := search(r'\ *\((.+)\)\ *', x)) is not None:
                    # Add the text in brackets to the explanation array
                    explanations.append(expl[1].strip())
                    # Remove the explanation from the original string
                    translations[i] = translations[i].replace(expl[0], ' ').strip()

                    # translations[i] = ' '.join([i.strip() for i in translations[i].split(expl[0])])
                    # This one is probably the best, but it looks boring
                    # translations[i] = translations[i][:expl.start()].strip() + ' ' + translations[i][expl.end():].strip()
                else:
                    # Append an empty string to avoid potential issues with explanation numbers
                    explanations.append(' ')

            row[1] = ';'.join(translations)
            row.append(';'.join(explanations) + ";")
            # print(row)
            writer.writerow(row)