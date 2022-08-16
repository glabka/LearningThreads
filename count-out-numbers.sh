# Counts how many numbers were stored. Spoiler: should be always the same
java -cp bin producer_processor_saver.MainMultiple | grep -E "Storing" | wc -l
