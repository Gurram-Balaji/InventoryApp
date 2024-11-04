import { render, screen, fireEvent } from '@testing-library/react';
import TableSupply from '../TableSupply'; // Adjust the import based on your file structure

describe('TableSupply Component', () => {
    const handleEditOpen = jest.fn();
    const handleDeleteOpen = jest.fn();
    
    it('renders no supply message when supply is empty', () => {
        render(<TableSupply loading={false} supply={[]} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />);
        expect(screen.getByText(/no supply found/i)).toBeInTheDocument();
    });

    it('renders supply data correctly', () => {
        const supplyData = [
            { itemId: 1, locationId: 101, supplyType: 'Type A', quantity: 50 },
            { itemId: 2, locationId: 102, supplyType: 'Type B', quantity: 30 },
        ];
        
        render(<TableSupply loading={false} supply={supplyData} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />);
        
        // Check if the table cells render correct data
        expect(screen.getByText(/Type A/i)).toBeInTheDocument();
        expect(screen.getByText(/50/i)).toBeInTheDocument();
        expect(screen.getByText(/Type B/i)).toBeInTheDocument();
        expect(screen.getByText(/30/i)).toBeInTheDocument();
    });

    it('triggers edit and delete actions', () => {
        const supplyData = [
            { itemId: 1, locationId: 101, supplyType: 'Type A', quantity: 50 },
        ];

        render(<TableSupply loading={false} supply={supplyData} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />);
        
        const editButton = screen.getByTestId('EditIcon');
        const deleteButton = screen.getByTestId('DeleteIcon');

        fireEvent.click(editButton);
        expect(handleEditOpen).toHaveBeenCalledWith(supplyData[0]);

        fireEvent.click(deleteButton);
        expect(handleDeleteOpen).toHaveBeenCalledWith(supplyData[0]);
    });
});
