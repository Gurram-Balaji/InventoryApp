import { errorToast, successToast } from '../../components/Toast';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Checkbox, FormControl, FormControlLabel, InputLabel, MenuItem, Select } from '@mui/material';
import apiClient from '../../components/baseUrl';


export default function EditForm({ openEditDialog, setOpenEditDialog, locationTypeOptions, fetchRow, page, setEditData, editData }) {
  const handleEditSave = async () => {
    try {
      const { locationId, locationDesc, locationType, addressLine1, city, state, country, pinCode } = editData;

      if (!locationId || !locationDesc || !locationType || !addressLine1 || !city || !state || !country || !pinCode) {
        errorToast("Please fill in all required fields.");
        return;
      }

      const response = await apiClient.patch(`/locations/${editData.locationId}`, editData);
      if (response.data.status === 404)
        errorToast(response.data.message);
      else if (response.data.success ) {
        successToast("Location updated successfully!");
        fetchRow(page);
      }

    } catch (error) {
      errorToast("Failed to update location");
    }
    setOpenEditDialog(false);
  };

  return (
     <Dialog PaperProps={{className: 'dialog-custom',  }}open={openEditDialog}  maxWidth="md" fullWidth>
      <DialogTitle className="dialog-title-custom" >Edit Location...</DialogTitle>
      <DialogContent style={{ padding: '30px 50px 10px' }}>
        {editData && (
          <TextField
            label="Location"
            fullWidth
            margin="normal"
            value={editData.locationId || ''}
            InputProps={{
              readOnly: true,
            style: {
              pointerEvents: 'none', // Prevent any interaction
            },
            }}
          />
        )}
        <TextField
          label="Description"
          fullWidth
          margin="normal"
          value={editData.locationDesc || ''}
          onChange={(e) => setEditData({ ...editData, locationDesc: e.target.value })}
        />

        {/* Location Type Select */}
          
          <FormControl fullWidth margin="dense"  >
          <InputLabel>Location Type</InputLabel>
          <Select label="Location Type"
            value={editData.locationType || ''}
            onChange={(e) => setEditData({ ...editData, locationType: e.target.value })}
            
          >
            {locationTypeOptions.map((type) => (
              <MenuItem key={type} value={type}>
                {type.toString().replace(/_/g, ' ') }
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {/* Address Fields */}
        <TextField
          label="Address Line 1"
          fullWidth
          margin="normal"
          
          value={editData.addressLine1 || ''}
          onChange={(e) => setEditData({ ...editData, addressLine1: e.target.value })}
        />
        <TextField
          label="Address Line 2"
          fullWidth
          margin="normal"
          
          value={editData.addressLine2 || ''}
          onChange={(e) => setEditData({ ...editData, addressLine2: e.target.value })}
        />
        <TextField
          label="Address Line 3"
          fullWidth
          margin="normal"
          
          value={editData.addressLine3 || ''}
          onChange={(e) => setEditData({ ...editData, addressLine3: e.target.value })}
        />
        <TextField
          label="City"
          fullWidth
          margin="normal"
          
          value={editData.city || ''}
          onChange={(e) => setEditData({ ...editData, city: e.target.value })}
        />
        <TextField
          label="State"
          fullWidth
          margin="normal"
          
          value={editData.state || ''}
          onChange={(e) => setEditData({ ...editData, state: e.target.value })}
        />
        <TextField
          label="Country"
          fullWidth
          margin="normal"
          
          value={editData.country || ''}
          onChange={(e) => setEditData({ ...editData, country: e.target.value })}
        />
        <TextField
          label="Pin Code"
          fullWidth
          margin="normal"
          
          value={editData.pinCode || ''}
          onChange={(e) => setEditData({ ...editData, pinCode: e.target.value })}
        />

        {/* Fulfillment Options */}
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
      <DialogActions style={{ display: 'flex', justifyContent: 'center', padding: '30px' }}>
        <Button
          onClick={() => setOpenEditDialog(false)}
          style={{
            backgroundColor: 'blue',
            color: 'white',
            fontWeight: 'bold',
            textTransform: 'uppercase',
            marginRight: '100px',
            padding: '10px 20px'
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleEditSave}
          style={{
            backgroundColor: 'green',
            color: 'white',
            fontWeight: 'bold',
            textTransform: 'uppercase',
            padding: '10px 20px'
          }}
        >
          Save
        </Button>
      </DialogActions>
    </Dialog>
  )
}