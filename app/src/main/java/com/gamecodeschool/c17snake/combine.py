import os
import glob

def combine_java_files(root_dir, output_file):
    # Use glob to find all Java files recursively
    java_files = glob.glob(os.path.join(root_dir, '**', '*.java'), recursive=True)
    
    with open(output_file, 'w', encoding='utf-8') as outfile:
        for java_file in java_files:
            # Write the file path to the output file
            outfile.write(f'File: {java_file}\n\n')
            
            # Read and write the content of the Java file
            with open(java_file, 'r', encoding='utf-8') as file:
                contents = file.read()
                outfile.write(contents)
                outfile.write('\n\n')  # Add extra newline for separation

if __name__ == "__main__":
    root_directory = r'C:\Users\Admin\Projects\Assignment_Snake_Game\app\src\main\java'
    output_filename = 'combined_java_files.txt'
    combine_java_files(root_directory, output_filename)
