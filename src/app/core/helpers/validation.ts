export function validateFields(
    data: Record<string, any>,
    requiredFields: readonly string[]
): boolean {

    return !requiredFields.some(    
        field =>
            data[field] === null ||
            data[field] === undefined ||
            data[field] === ''
    );
}