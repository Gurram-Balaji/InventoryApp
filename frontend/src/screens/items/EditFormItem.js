import { errorToast, successToast } from '../../components/Toast';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Checkbox, FormControl, FormControlLabel, InputLabel, MenuItem, Select } from '@mui/material';
import apiClient from '../../components/baseUrl';

export default function EditFormItem({ openEditDialog, editData, statusOptions, fetchItems, page, setOpenEditDialog, setEditData }) {
  const handleEditSave = async () => {
    try {
      const { itemid, itemDescription, category, type, price } = editData;

      // Validate required fields
      if (!itemid || !itemDescription || !category || !type || price === undefined) {
        errorToast("Please fill in all required fields.");
        return;
      }

      // Send API request to update item
      const response = await apiClient.patch(`/items/${itemid}`, editData);
      if (response.data.status === 404) {
        errorToast(response.data.message);
      } else if (response.data.success) {
        successToast("Item updated successfully!");
        fetchItems(page); // Refresh items after edit
      }
    } catch (error) {
      errorToast("Failed to update item");
    }
    setOpenEditDialog(false);
  };

  return (
    <Dialog
      PaperProps={{ className: 'dialog-custom' }}
      open={openEditDialog}
      onClose={() => setOpenEditDialog(false)}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle className="dialog-title-custom">Edit Item...</DialogTitle>
      <DialogContent style={{ padding: '20px 40px' }}>
        {editData && (
          <TextField
            label="Item ID"
            fullWidth
            margin="normal"
            value={editData.itemid || ''}
            InputProps={{
              readOnly: true,
              style: {
                backgroundColor: '#f5f5f5', // Light gray background
                pointerEvents: 'none', // Prevent interaction
              },
            }}
          />
        )}
        <TextField
          label="Description"
          fullWidth
          margin="normal"
          value={editData.itemDescription || ''}
          onChange={(e) => setEditData({ ...editData, itemDescription: e.target.value })}
          variant="outlined" // Use outlined style
          required // Indicate required field
        />
        <TextField
          label="Category"
          fullWidth
          margin="normal"
          value={editData.category || ''}
          onChange={(e) => setEditData({ ...editData, category: e.target.value })}
          variant="outlined" // Use outlined style
          required // Indicate required field
        />
        <TextField
          label="HSN Code"
          fullWidth
          margin="normal"
          value={editData.type || ''}
          onChange={(e) => setEditData({ ...editData, type: e.target.value })}
          variant="outlined" // Use outlined style
          required // Indicate required field
        />
        <FormControl fullWidth margin="normal" variant="outlined">
          <InputLabel>Status</InputLabel>
          <Select
            label='Status'
            value={editData.status || ''}
            onChange={(e) => setEditData({ ...editData, status: e.target.value })}
            required // Indicate required field
          >
            {statusOptions.map((status) => (
              <MenuItem key={status} value={status}>
                {status}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <TextField
          label="Price"
          fullWidth
          margin="normal"
          type="number"
          value={editData.price}
          onChange={(e) => setEditData({ ...editData, price: Math.max(0, e.target.value) })} // Ensure non-negative value
          variant="outlined" // Use outlined style
          required // Indicate required field
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={editData.pickupAllowed || false}
              onChange={(e) => setEditData({ ...editData, pickupAllowed: e.target.checked })}
            />
          }
          label="Pickup Allowed"
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={editData.shippingAllowed || false}
              onChange={(e) => setEditData({ ...editData, shippingAllowed: e.target.checked })}
            />
          }
          label="Shipping Allowed"
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={editData.deliveryAllowed || false}
              onChange={(e) => setEditData({ ...editData, deliveryAllowed: e.target.checked })}
            />
          }
          label="Delivery Allowed"
        />
      </DialogContent>
      <DialogActions style={{ display: 'flex', justifyContent: 'center', padding: '20px' }}>
        <Button
          onClick={() => setOpenEditDialog(false)}
          style={{
            backgroundColor: '#1976d2', // Primary color
            color: 'white',
            fontWeight: 'bold',
            textTransform: 'uppercase',
            padding: '10px 20px',
            marginRight: '10px',
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleEditSave}
          style={{
            backgroundColor: '#4caf50', // Success color
            color: 'white',
            fontWeight: 'bold',
            textTransform: 'uppercase',
            padding: '10px 20px',
          }}
        >
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
}
