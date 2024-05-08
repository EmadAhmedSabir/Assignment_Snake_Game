import os

# Set the directory where the Java files are located
directory = r'C:\Users\Admin\Projects\Emad\Assignment_Snake_Game\app\src\main\java\com\gamecodeschool\c17snake'

# Open the output file for writing
with open('combined_java_files.txt', 'w') as output_file:
    # Loop through each file in the directory
    for filename in os.listdir(directory):
        # Check if the file is a Java file
        if filename.endswith('.java'):
            # Get the full path of the file
            file_path = os.path.join(directory, filename)
            
            # Write the file name and location to the output file
            output_file.write(f'File: {file_path}\n')
            
            # Write the contents of the file to the output file
            with open(file_path, 'r') as input_file:
                output_file.write(input_file.read())
            
            # Add a blank line between files
            output_file.write('\n')

print('Combined Java files saved to "combined_java_files.txt".')